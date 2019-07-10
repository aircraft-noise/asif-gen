(ns analyze.airports
  (:require
   [clojure.java.io :as io]
   ))

;;
;; Read in the airports from a resource file
;;

(def sfba
  (-> "airports-sfba.edn"
      io/resource
      slurp
      clojure.edn/read-string))

(def sfba-by-airport
  (let [gb (group-by (comp name :code) sfba)]
    (zipmap (keys gb)
            (map first (vals gb)))))
