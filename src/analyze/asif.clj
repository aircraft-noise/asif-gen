(ns analyze.asif
  (:require
   [analyze.airports :as airports]
   [analyze.waypoints :as waypoints]
   [clojure.string :as string]
   [clojure.data.xml :as data.xml]
   [slingshot.slingshot :refer [throw+ try+]]
   [clojure.set :as set]
   ))

;; https://github.com/clojure/data.xml

(defn known?
  [s]
  (not= s "unknown"))

(def unknown? (complement known?))

(defn arrival-or-departure
  "TODO: Make sure origin/destination are in the SF Bay Area!!!"
  [{:keys [origin destination]}]
  (if (and (known? destination)
           (unknown? origin))
    :arrival
    (if (and (known? origin)
             (unknown? destination))
      :departure
      false)))

(defn arrival?
  [m]
  (if (= :arrival (arrival-or-departure m))
    true
    false))

(defn departure?
  [m]
  (if (= :departure (arrival-or-departure m))
    true
    false))

(defn airport-layout
  [airport]
  [:airportLayout [:airportCode airport]])

(defn scenario-airport-layout
  [airport]
  [:scenarioAirportLayout [:airportLayoutName airport]])

(defmulti receptor
  (fn [{:keys [type]}]
    (if (keyword? type)
      type
      (keyword type))))

(defmethod receptor :point-receptor
  [{:keys [name lat lon elevation]}]
  [:pointReceptor
   [:name name]
   [:latitude lat]
   [:longitude lon]
   [:elevation elevation]])

(defmethod receptor :grid
  [{:keys [lat lon width height num-width num-height]}]
  [:grid
   [:latitude lat]
   [:longitude lon]
   [:width width]
   [:height height]
   [:numWidth num-width]
   [:numHeight num-height]])

(defn receptor-set
  [{:keys [name contents]}]
  (into [:receptorSet
         [:name name]]
        (mapv receptor contents)))

(defn az
  "TODO: very primative, stubbbed out for initial dev"
  [{:keys [name weight]}]
  [:annualization
   [:name name]
   [:annualizationGroup
    [:weight weight]
    [:annualizationCase
     [:name name]
     [:weight weight]]]])

(defmulti operation
  (fn [{:keys [op-type]}]
    op-type))

(defmethod operation :arrival
  [{:keys [id aircraft-id number op-type airport runway op-time]}]
  [:operation
   [:id id]
   [:aircraftType
    [:anpAircraftId aircraft-id]]
   [:numOperations number]
   ;; [:arrivalAirport {:type "ICAO"} airport]
   [:arrivalAirport airport]
   [:arrivalRunway (if runway
                     runway
                     (->> airport
                          (get airports/sfba-by-airport)
                          :runways
                          :arrival
                          first))]
   [:onTime op-time]])

(defmethod operation :departure
  [{:keys [id aircraft-id number op-type airport runway op-time]}]
  [:operation
   [:id id]
   [:aircraftType
    [:anpAircraftId aircraft-id]]
   [:numOperations number]
   ;; [:saeProfile "STANDARD"]
   ;; [:stageLength 1]
   ;; [:departureAirport {:type "ICAO"} airport]
   [:departureAirport airport]
   [:departureRunway (if runway
                       runway
                       (->> airport
                            (get airports/sfba-by-airport)
                            :runways
                            :departure
                            first))]
   [:offTime op-time]
   [:saeProfile "STANDARD"]
   [:stageLength 1]
   ])

(defn operations-gen
  [operations]
  (into [:operations]
        (mapv operation operations)))

(defmulti format-operation
  arrival-or-departure)

(defmethod format-operation :arrival
  [{:keys [icao tail ac-type ac-id origin destination flight ts-start ts-end] :as op-map}]
  [:operations
   (operation {:id flight
               :aircraft-id (if ac-id
                              ac-id
                              (->> destination
                                   (get airports/sfba-by-airport)
                                   :default-aircraft))
               :op-type :arrival
               :number 1.0
               :airport destination
               :op-time ts-end})])

(defmethod format-operation :departure
  [{:keys [icao tail ac-type ac-id origin destination flight ts-start ts-end] :as op-map}]
  [:operations
   (operation {:id flight
               :aircraft-id (if ac-id
                              ac-id
                              (->> origin
                                   (get airports/sfba-by-airport)
                                   :default-aircraft))
               :op-type :departure
               :number 1.0
               :airport origin
               :op-time ts-start})])

(defn track-node
  [{:keys [lat lon] :as node}]
  [:trackNode
   [:latitude lat]
   [:longitude lon]])

(defn subtrack
  [{:keys [id dispersion-weight nodes]}]
  [:subtrack
   [:id id]
   [:dispersionWeight dispersion-weight]
   (into [:trackNodes]
         (mapv track-node nodes))])

(defn track-gen
  [{:keys [name op-type airport runway subtracks]}]
  (into [:track
         [:name name]
         [:optype op-type]
         ;; [:airport {:type "ICAO"} airport]
         [:airport airport] ;; Removed {:type "ICAO"} due to errors Kadin found with AEDT, 2019-06
         [:runway runway]]
        (mapv subtrack subtracks)))

(defmulti format-track
  arrival-or-departure)

(defmethod format-track :arrival
  [{:keys [icao tail origin destination flight ts-start ts-end positions]}]
  (track-gen {:name flight
              :op-type "A"
              :airport destination
              :runway (->> destination
                           (get airports/sfba-by-airport)
                           :runways
                           :arrival
                           first)
              :subtracks [{:id 0
                           :dispersion-weight 1.0
                           :nodes positions}]}))

(defmethod format-track :departure
  [{:keys [icao tail origin destination flight ts-start ts-end positions]}]
  (track-gen {:name flight
              :op-type "D"
              :airport origin
              :runway (->> origin
                           (get airports/sfba-by-airport)
                           :runways
                           :departure
                           first)
              :subtracks [{:id 0
                           :dispersion-weight 1.0
                           :nodes positions}]}))

(defn track-op-set
  [{:keys [track operations]}]
  [:trackOpSet]
  (track-gen track)
  (operations-gen operations))

(defn format-track-op-set-track-nodes
  [m]
  [:trackOpSet
   (format-track m)
   (format-operation m)])

(defn generate-tos-track-nodes
  [ms]
  (mapv format-track-op-set-track-nodes ms))

(defn case-gen
  [{:keys [id name description source start-time stop-time duration track-op-sets]}]
  (into [:case
         [:caseId id]
         [:name name]
         [:source source]
         [:startTime start-time]
         [:duration duration]
         [:description description]]
        track-op-sets))

(defn scenario-gen
  [{:keys [name
           start-time
           duration
           taxi-model
           ac-perf-model
           bank-angle
           alt-cutoff
           sulfur-conv-rate
           fuel-sulfur-content
           description
           airports
           cases
           annualization]}]
  [:scenario
   [:name name]
   [:startTime start-time]
   [:duration duration]
   [:taxiModel taxi-model]
   [:acftPerfModel ac-perf-model]
   [:bankAngle bank-angle]
   [:altitudeCutoff alt-cutoff]
   [:sulfurConversionRate sulfur-conv-rate]
   [:fuelSulfurContent fuel-sulfur-content]
   [:description description]
   (into [:scenarioAirportLayoutSet] (mapv scenario-airport-layout airports))
   (into [:caseSet] (mapv case-gen cases))
   (az annualization)])

(defn study
  [{:keys [name study-type emission-units description airports receptor-sets scenario]}]
  (-> [:study {:xmlns:asif "http://www.faa.gov/ASIF"}
       [:name name]
       [:studyType study-type]
       [:emissionsUnits emission-units]
       [:description description]
       (into [:airportLayoutSet] (mapv airport-layout airports))]
      (into (mapv receptor-set receptor-sets))
      (conj (scenario-gen scenario))))

(defn header
  [content]
  [:AsifXml
   {:xmlns/AsifXml "http://www.faa.gov/ASIF"
    :xmlns/xsi "http://www.w3.org/2001/XMLSchema-instance"
    :version "1.2.10"
    :content "study"}
   content])
