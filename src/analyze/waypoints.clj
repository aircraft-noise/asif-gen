(ns analyze.waypoints
  (:require
   [clojure.java.io :as io]
   ))

;;
;; Read in the waypoints from a resource file
;;

(def sfba
  (-> "waypoints-sfba.edn"
      io/resource
      slurp
      clojure.edn/read-string))

(def sfba-by-code
  (let [gb (group-by :code sfba)]
    (zipmap (keys gb)
            (map first (vals gb)))))

(def sfba-by-name
  (let [gb (group-by :name sfba)]
    (zipmap (keys gb)
            (map first (vals gb)))))
