(ns analyze.tcr
  (:require
   [analyze.json :as json]
   [analyze.asif :as asif]
   [clojure.string :as string]
   [clojure.data.xml :as data.xml]
   [slingshot.slingshot :refer [throw+ try+]]
   [clojure.set :as set]
   [clj-time.core :as time]
   [clj-time.format :as time.format]
   [clj-time.local  :as time.local]
   [clj-time.coerce :as time.coerce]))

;;  https://www.nist.gov/pml/time-and-frequency-division/popular-links/daylight-saving-time-dst
;;    begins at 2:00 a.m. on the second Sunday of March and
;;    ends at 2:00 a.m. on the first Sunday of November

(defn pdt-epoch->time
  "TODO: FIX: assumes 7 hour offset between Pacific time and UTC"
  [t]
  (time.coerce/from-long (* 1000 (- (long t) (* 7 60 60)))))

(defn epoch-seconds->time
  [t]
  (-> t
      (* 1000)
      long
      time.coerce/from-long))

(defn asif-time
  [t]
  (time.format/unparse (time.format/formatter :date-hour-minute-second) t))

(defn pdt-epoch->asif-time
  [t]
  (-> t pdt-epoch->time asif-time))

(defn split-segments
  [metadata [header & positions]]
  {:metadata metadata
   :header header
   :positions (first positions)})

(defn split-icaos
  [m]
  (reduce-kv (fn
               [acc k [metadata & segments]]
               (into acc (mapv #(split-segments metadata %) segments)))
             []
             m))

(defn format-position
  [{:keys [seen_pos] :as position}]
  (assoc position
         :time (pdt-epoch->asif-time seen_pos)))

(defn format-aircraft
  [{:keys [metadata header positions]}]
  (let [{:keys [tail_# ac_type icao #_segments]} metadata
        {:keys [#_sightings gps_min segment_start segment gap origin segment_end flight destination gps_max]} header]
    {:icao icao
     :segment segment
     :tail tail_#
     :origin origin
     :ac-type ac_type
     :destination destination
     :flight flight
     :ts-start (pdt-epoch->asif-time segment_start)
     :ts-end (pdt-epoch->asif-time segment_end)
     :utc-start (epoch-seconds->time segment_start)
     :utc-end (epoch-seconds->time segment_end)
     :positions (mapv format-position positions)
     }))

(defn file->aircraft
  [filename]
  (-> filename
      json/file->edn
      :aircraft
      split-icaos))

(def unsupported-airports
  (set ["SUMED" "VMCED"]))

(defn unsupported-airport?
  "TODO: horrible hack"
  [m]
  (or (contains? unsupported-airports (:origin m))
      (contains? unsupported-airports (:destination m))))

(defn ->aircraft
  ([filename]
   (->aircraft identity filename))
  ([predicate filename]
   (->> filename
        file->aircraft
        (map format-aircraft)
        (remove unsupported-airport?)
        (filterv predicate))))

(defn get-earliest-utc-start
  [ms]
  (->> ms
       (map :utc-start)
       time/earliest))

(defn get-latest-utc-end
  [ms]
  (->> ms
       (map :utc-end)
       time/latest))

(defn file-flight-times
  [filename]
  (let [flights (->> filename
                     file->aircraft
                     (mapv format-aircraft))
        min-start (get-earliest-utc-start flights)
        max-end (get-latest-utc-end flights)]
    (println "File:" filename)
    (println)
    (println "Start of day, UTC " (asif-time (time.local/to-local-date-time (time/date-time 2019 06 19))))
    (println "Earliest in file: " (asif-time min-start))
    (println)
    (println "End of day, UTC:  " (asif-time (time.local/to-local-date-time (time/date-time 2019 06 19 23 59 59))))
    (println "Latest in file:   " (asif-time max-end))))

;; (def acf (->aircraft "./data/aedt/FA_Noise_Examples.180401.json"))

(defn get-airports
  [ms]
  (let [all-airports (set (into (mapv :origin ms) (mapv :destination ms)))]
    (vec (set/difference all-airports #{"unknown"}))))

(defn get-earliest-start
  "TODO: use min/max instead of sort"
  [ms]
  (->> ms
       (map :ts-start)
       (sort compare)
       first))

(defn get-latest-end
  "TODO: use min/max instead of sort"
  [ms]
  (->> ms
       (map :ts-end)
       (sort #(compare %2 %1))
       first))

(defn get-ac-types
  [ms]
  (vec (set (map :ac-type ms))))

(defn get-orig-dest
  [ms]
  (->> ms
       (map (juxt :origin :destination))
       set
       vec))

(defn get-orig-dest-freq
  [ms]
  (->> ms
       (mapv (juxt :origin :destination))
       frequencies))

(def stats
  {["KSJC" "KSJC"] 1,
   ["KSFO" "unknown"] 485,
   ["KSJC" "KSQL"] 1,
   ["KSQL" "KSQL"] 8,
   ["KSQL" "unknown"] 32,
   ["KSQL" "KPAO"] 1,
   ["unknown" "KPAO"] 86,
   ["KSJC" "unknown"] 139,
   ["unknown" "KSQL"] 60,
   ["KPAO" "KSQL"] 2,
   ["KPAO" "unknown"] 58,
   ["unknown" "KSJC"] 197,
   ["unknown" "KSFO"] 610,
   ["KPAO" "KPAO"] 16,
   ["unknown" "unknown"] 1589})

(def arrival-stats
  {["unknown" "KPAO"] 86,
   ["unknown" "KSQL"] 60,
   ["unknown" "KSJC"] 197,
   ["unknown" "KSFO"] 610,})

(def departure-stats
  {["KSFO" "unknown"] 485,
   ["KSQL" "unknown"] 32,
   ["KSJC" "unknown"] 139,
   ["KPAO" "unknown"] 58,})

;; airport code is 15CA, not SUMED
;; Valley Medical Center in San Jose. if not mistaken
;; 86CA  is the code for that
