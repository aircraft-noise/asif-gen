(ns analyze.tcr
  (:require
   [analyze.json :as json]
   [clojure.string :as string]
   [clojure.data.xml :as data.xml]
   [slingshot.slingshot :refer [throw+ try+]]
   [clojure.set :as set]
   [clj-time.core :as time]
   [clj-time.format :as time.format]
   [clj-time.local  :as time.local]
   [clj-time.coerce :as time.coerce]))

(defn pdt-epoch->time
  [t]
  (time.coerce/from-long (* 1000 (- (long t) (* 7 60 60)))))

(defn asif-time
  [t]
  (time.format/unparse (time.format/formatter :date-hour-minute-second) t))

(defn pdt-epoch->asif-time
  [t]
  (-> t pdt-epoch->time asif-time))

(defn file->aircraft
  [filename]
  (-> filename
      json/file->edn
      :aircraft
      vals))

(defn format-aircraft
  [v]
  (let [{:keys [tail_# ac_type icao #_segments] :as metadata} (first v)
        {:keys [#_sightings gps_min segment_start segment gap origin segment_end flight destination gps_max] :as header} (-> v second first)
        positions (-> v second rest first)]
    {:icao icao
     :tail tail_#
     :origin origin
     :destination destination
     :flight flight
     :ts-start (pdt-epoch->asif-time segment_start)
     :ts-end (pdt-epoch->asif-time segment_end)
     :positions positions
     }))

(defn ->aircraft
  [filename]
  (->> filename
       file->aircraft
       (mapv format-aircraft)))

;; (def acf (->aircraft "./data/aedt/FA_Noise_Examples.180401.json"))
