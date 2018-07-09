(ns analyze.asif
  (:require
   [clojure.string :as string]
   [clojure.data.xml :as data.xml]
   [slingshot.slingshot :refer [throw+ try+]]
   [clojure.set :as set]
   ))

;; https://github.com/clojure/data.xml

(defn airport-layout
  [airport]
  [:airportLayout [:airportCode airport]])

(defn scenario-airport-layout
  [airport]
  [:scenarioAirportLayout [:airportLayoutName airport]])

(defmulti receptor
  (fn [{:keys [type]}]
    type))

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

(defn operation
  "TODO: Only handles arrivals currently, needs major work!"
  [{:keys [id aircraft-id number arrival-airport arrival-runway on-time]}]
  [:operation
   [:id id]
   [:aircraftType
    [:anpAircraftId aircraft-id]]
   [:numOperations number]
   [:arrivalAirport {:type "ICAO"} arrival-airport]
   [:arrivalRunway arrival-runway]
   [:onTime on-time]])

(defn operations-gen
  [operations]
  (into [:operations]
        (mapv operation operations)))

(defn format-operation
  [{:keys [icao tail origin destination flight ts-start ts-end]}]
  [:operations
   (operation {:id flight
               :aircraft-id "737800"
               :number 1.0
               :arrival-airport destination
               :arrival-runway "28L"
               :on-time ts-end})])

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
         [:airport {:type "ICAO"} airport]
         [:runway runway]]
        (mapv subtrack subtracks)))

(defn format-track
  [{:keys [icao tail origin destination flight ts-start ts-end positions]}]
  (track-gen {:name flight
              :op-type "A"
              :airport destination
              :runway "28L"
              :subtracks [{:id 0
                           :dispersion-weight 1.0
                           :nodes positions}]}))

(defn track-op-set
  [{:keys [track operations]}]
  [:trackOpSet]
  (track-gen track)
  (operations-gen operations))

(defn format-track-op-set
  [m]
  [:trackOpSet
   (format-track m)
   (format-operation m)])

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
