(ns analyze.job
  (:require
   [analyze.tcr :as tcr]
   [analyze.asif :as asif]
   [clojure.string :as string]
   [clojure.data.xml :as data.xml]
   [slingshot.slingshot :refer [throw+ try+]]
   ))

;; https://github.com/clojure/data.xml

(defn ->xml
  [f]
  (-> f
      data.xml/sexp-as-element
      data.xml/indent-str))

(defn ->asif
  [ms]
  (asif/header
   (asif/study {:name "SFO Single Plane Test"
                :study-type "Noise and Dispersion"
                :emission-units "Kilograms"
                :description "One Sensor Path Operation Near SFO"
                :airports ["KSFO"]
                :receptor-sets [{:name "MONA sensors"
                                 :contents [{:type :point-receptor
                                             :name "DCJ"
                                             :lat 37.444713
                                             :lon -122.155651
                                             :elevation 49}
                                            {:type :point-receptor
                                             :name "TCR"
                                             :lat 37.450204
                                             :lon -122.143786
                                             :elevation 26}]}
                                {:name "Bay Grid Sensors"
                                 :contents [{:type :grid
                                             :lat 37.296680
                                             :lon -122.787444
                                             :width 50.0
                                             :height 50.0
                                             :num-width 100
                                             :num-height 100}]}]
                :scenario {:name "aa56b6 Overflight"
		           :description "Asif Import Test, Single Flight"
		           :start-time "2018-04-01T00:00:00"
		           :duration 24
		           :taxi-model "UserSpecified"
		           :ac-perf-model "SAE1845"
		           :bank-angle true
		           :alt-cutoff 42000
		           :sulfur-conv-rate 0.05
                           :fuel-sulfur-content 6.8E-4
                           :airports ["KSFO"]
                           :annualization {:name "Auto Ops"
                                           :weight 1.0}
                           :cases [{:id 1
                                    :name "Auto Ops"
                                    :description "Sample"
                                    :source "Aircraft"
                                    :start-time "2018-04-01T00:00:00"
                                    :duration 24
                                    :track-op-sets (mapv asif/format-track-op-set ms)}]
                           }})))

(defn generate-file
  ([infile outfile]
   (generate-file asif/arrival-or-departure infile outfile))
  ([predicate infile outfile]
   (->> infile
        (tcr/->aircraft predicate)
        ->asif
        ->xml
        (spit outfile))))

(defn generate-departures-file
  [infile outfile]
  (generate-file asif/departure? infile outfile))

(defn generate-arrivals-file
  [infile outfile]
  (generate-file asif/arrival? infile outfile))

(def full-file "./data/aedt/FA_Sightings.180401.airport_ids.json")

;; (generate-file asif/arrival? full-file "arrivals.xml")

;; (generate-file asif/departure? full-file "departures.xml")

;; (generate-file asif/arrival-or-departure full-file "both.xml")
