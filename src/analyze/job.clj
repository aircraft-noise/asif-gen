(ns analyze.job
  (:require
   [analyze.tcr :as tcr]
   [analyze.asif :as asif]
   [analyze.airports :as airports]
   [analyze.waypoints :as waypoints]
   [analyze.yaml :as yaml]
   [analyze.json :as json]
   [analyze.edn]
   [me.raynes.fs :as fs]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [clojure.data.xml :as data.xml]
   [clojure.tools.cli :as cli]
   [slingshot.slingshot :refer [throw+ try+]])
  (:gen-class
   :main true))
;;   :name asif.gen))

;; https://github.com/clojure/data.xml

;; https://github.com/clojure/tools.cli

(def dkw->dsfn
  {:!get-airports! #'tcr/get-airports
   :!generate-tos-track-nodes! #'asif/generate-tos-track-nodes})

(defn ^:private add-string-keys
  [m]
  (into m (zipmap (map clojure.core/name (keys m))
                  (vals m))))

(let [with-strings (add-string-keys dkw->dsfn)
      dkws (-> with-strings
               keys
               set)]

  (defn ^:private dkw?
    [k]
    (contains? dkws k))

  (defn ^:private eval-dsfn
    [data dkw]
    ((get with-strings dkw) data))

)

(defn ^:private replace-dkws
  "Recursively transforms all map DSL values into functions"
  [data m]
  (let [f (fn [[k v]] (if (dkw? v) [k (eval-dsfn data v)] [k v]))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn ->xml
  [f]
  (-> f
      data.xml/sexp-as-element
      data.xml/indent-str))

(defn ->study
  [data dsl]
  (->> dsl
       (replace-dkws data)
       asif/study
       asif/header))

(defn KSFO?
  [{:keys [origin destination]}]
  (or (= origin "KSFO")
      (= destination "KSFO")))

(defn KOAK?
  [{:keys [origin destination]}]
  (or (= origin "KOAK")
      (= destination "KOAK")))

(defn KSJC?
  [{:keys [origin destination]}]
  (or (= origin "KSJC")
      (= destination "KSJC")))

(defn only-one
  [v]
  (take 1 v))

(def filter->fn
  {:KSFO #'KSFO?
   :KOAK #'KOAK?
   :KSJC #'KSJC?
   :both #'asif/arrival-or-departure
   :arrivals #'asif/arrival?
   :departures #'asif/departure?})

(defn read-flights-file
  ([infile]
   (read-flights-file asif/arrival-or-departure infile))
  ([predicate infile]
   (tcr/->aircraft predicate infile)))

(defn one-sfo-flight
  "Example filterfn for generate-file-filtered"
  [v]
  (->> v
       (filter KSFO?)
       (take 1)))

(def flights-file
  "./data/flights/flights-20180401.json")

(def study-file
  "data/examples/tracknode-study.edn")

(defn read-structured-file
  [filename]
  (case (fs/extension filename)
    ".edn"  (analyze.edn/file-> filename)
    ".yaml" (yaml/file->edn filename)
    ".json" (json/file->edn filename)))

(defn process-study
  ([study-file flights-file out-file]
   (process-study asif/arrival-or-departure study-file flights-file out-file))
  ([filter-fn study-file flights-file out-file]
   (->> study-file
        read-structured-file
        (->study (read-flights-file filter-fn flights-file))
        ->xml
        (spit out-file))))

(defn process-study-special
  [study-file flights-data out-file]
  (->> study-file
       read-structured-file
       (->study flights-data)
       ->xml
       (spit out-file)))

(defn reformat-airport
  [{:keys [runways runways-reverse default-aircraft] :as airport}]
  (assoc (merge airport runways)
         :acft default-aircraft
         :arr-rev (:arrival runways-reverse)
         :dprt-rev (:departure runways-reverse)))

(defn airport-table
  []
  (->> airports/sfba
       (mapv reformat-airport)
       (clojure.pprint/print-table [:code :acft :arrival :departure :arr-rev :dprt-rev :name])))

(def cli-options
  [[nil "--study file" "Study filename, supported formats/extensions: yaml, json, and edn"]
   [nil "--flights file" "Flights filename (TCR-JSON)"]
   [nil "--filter name" "Name of filter to invoke on flights, this option can provided multiple times..."
    :default []
    :parse-fn #(keyword %)
    :assoc-fn (fn [m k v] (update-in m [k] conj v))]
   [nil "--output file" "Filename for generated ASIF"]
   ["-h" "--help"]])

(defn usage
  [options-summary]
  (->> ["Generates AEDT ASIF XML from the provided study template and flights data."
        ""
        "Usage: asif-gen [options]"
        ""
        "Options:"
        options-summary
        ""
        "Please refer to the README for more information:"
        "  https://github.com/aircraft-noise/asif-gen/blob/develop/README.md"]
       (string/join \newline)
       println))

(defn -main
  [& args]
  (let [{:keys [options arguments summary errors]} (cli/parse-opts args cli-options)
        {:keys [study flights filter out help]} options]
    (cond
      help (do
             (usage summary)
             (System/exit 0))
      options (do
                (let [filter-names (if (empty? filter) [:both] filter)
                      filter-fns (apply comp (reverse (map filter->fn filter-names)))]
                  (process-study filter-fns study flights out))))))

(defn edn-test-data
  []
  (-> flights-file
      read-flights-file
      (analyze.edn/->file "flights.edn")))


(defn tcr-edn
  [filename]
  (-> filename
      (str ".json")
      read-flights-file
      (analyze.edn/->file (str filename ".edn"))))
