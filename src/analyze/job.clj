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
   [slingshot.slingshot :refer [throw+ try+]]
   ))

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
  [{:keys [origin departure]}]
  (or (= origin "KSFO")
      (= departure "KSFO")))

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

(def full-file
  "./data/flights/FA_Sightings.180401.airport_ids.json")

(defn read-structured-file
  [filename]
  (case (fs/extension filename)
    ".edn"  (analyze.edn/file-> filename)
    ".yaml" (yaml/file->edn filename)
    ".json" (json/file->edn filename)))

(defn process-study
  [study-file flights-file out-file]
  (->> study-file
       read-structured-file
       (->study (read-flights-file flights-file))
       ->xml
       (spit out-file)))
