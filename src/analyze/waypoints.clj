(ns analyze.waypoints)

(def sfba
  [{:code :EDDYY :name "EDDYY" :lat 37.374903 :lon -122.119028 :version 3}
   {:code :MENLO :name "MENLO" :lat 37.463694 :lon -122.153667}
   {:code :AMEBY :name "AMEBY" :lat 37.371828 :lon -122.058100}
   {:code :ARCHI :name "ARCHI" :lat 37.490806 :lon -121.875556}
   {:code :SIDBY :name "SIDBY" :lat 37.450711 :lon -122.144722}
   {:code :EDDYY2 :name "EDDYY2" :lat 37.326444 :lon -122.099722 :version 2}
   {:code :NARWL :name "NARWL" :lat 37.274781 :lon -122.079306}
   {:code :DUMBA :name "DUMBA" :lat 37.503517 :lon -122.096147}
   {:code :FAITH :name "FAITH" :lat 37.401217 :lon -121.861900}
   {:code :TRDOW :name "TRDOW" :lat 37.493828 :lon -121.985542}])

(def sfba-by-code
  (let [gb (group-by :code sfba)]
    (zipmap (keys gb)
            (map first (vals gb)))))

(def sfba-by-name
  (let [gb (group-by :name sfba)]
    (zipmap (keys gb)
            (map first (vals gb)))))
