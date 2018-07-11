(ns analyze.airports)

(def sfba
  "https://en.wikipedia.org/wiki/List_of_airports_in_the_San_Francisco_Bay_Area"
  [{:code :KSFO
    :name "San Francisco International Airport"
    :lat 37.618889
    :lon -122.375
    :altitude 4
    :default-aircraft "737800"
    :runways {:arrival ["28L"]
              :departure ["01R"]}
    :classes [:b]}
   {:code :KOAK
    :name "Oakland International Airport"
    :lat 37.721389
    :lon -122.220833
    :altitude 3
    :default-aircraft "737800"
    :runways {:arrival ["30"]
              :departure ["30"]}
    :classes [:c]}
   {:code :KSJC
    :name "Norman Y. Mineta San José International Airport"
    :lat 37.362778
    :lon -121.929167
    :altitude 19
    :default-aircraft "737800"
    :runways {:arrival ["30R"]
              :departure ["30L"]}
    :runways-reverse {:arrival ["12R"]
                      :departure ["12L"]}
    :classes [:c]}
   {:code :KNUQ
    :name "Moffett Federal Airfield"
    :lat 37.415
    :lon -122.048333
    :altitude 11
    :default-aircraft "GV"
    :runways {:arrival ["32R"]
              :departure ["32R"]}
    :classes [:d]}
   {:code :KSUU
    :name "Travis Air Force Base"
    :lat 38.262778
    :lon -121.9275
    :altitude 19
    :classes [:d]}
   {:code :KCCR
    :name "Concord/Buchanan Field Airport"
    :lat 37.989722
    :lon -122.056944
    :altitude 8
    :classes [:d]}
   {:code :KHWD
    :name "Hayward Executive Airport"
    :lat 37.658889
    :lon -122.121667
    :default-aircraft "CNA172"
    :altitude 16
    :runways {:arrival ["28R"]
              :departure ["28R"]}
    :classes [:d]}
   {:code :KLVK
    :name "Livermore Municipal Airport"
    :lat 37.693389
    :lon -121.820361
    :altitude 122
    :classes [:d]}
   {:code :KAPC
    :name "Napa County Airport"
    :lat 38.213194
    :lon -122.280694
    :altitude 11
    :classes [:d]}
   {:code :KPAO
    :name "Palo Alto Airport"
    :lat 37.461111
    :lon -122.115
    :altitude 2
    :default-aircraft "CNA172"
    :runways {:arrival ["31"]
              :departure ["31"]}
    :classes [:d]}
   {:code :KRHV
    :name "Reid-Hillview Airport"
    :lat 37.332778
    :lon -121.819722
    :altitude 40.5
    :default-aircraft "CNA172"
    :runways {:arrival ["31R"]
              :departure ["31R"]}
    :classes [:d]}
   {:code :KSQL
    :name "San Carlos Airport"
    :lat 37.511944
    :lon -122.249444
    :altitude 2
    :default-aircraft "CNA172"
    :runways {:arrival ["30"]
              :departure ["30"]}
    :classes [:d]}
   {:code :KSTS
    :name "Charles M. Schulz - Sonoma County Airport"
    :lat 38.508889
    :lon -122.812778
    :altitude 39
    :classes [:d]}
   {:code :2O3
    :name "Angwin-Parrett Field"
    :lat 38.58
    :lon -122.435556
    :altitude 563
    :classes [:e :g]}
   {:code :C83
    :name "Byron Airport"
    :lat 37.828333
    :lon -121.625833
    :altitude 24
    :classes [:e :g]}
   {:code :O60
    :name "Cloverdale Municipal Airport"
    :lat 38.776111
    :lon -122.9925
    :altitude 25
    :classes [:e :g]}
   {:code :KDVO
    :name "Gnoss Field"
    :lat 38.143611
    :lon -122.556111
    :altitude 0.6
    :classes [:e :g]}
   {:code :KHAF
    :name "Half Moon Bay Airport"
    :lat 37.513333
    :lon -122.501111
    :altitude 20
    :classes [:e :g]}
   {:code :KHES
    :name "Healdsburg Municipal Airport"
    :lat 38.653611
    :lon -122.899444
    :altitude 84.7
    :classes [:e :g]}
   {:code :KCVH
    :name "Hollister Municipal Airport"
    :lat 36.893333
    :lon -121.410278
    :altitude 70
    :classes [:e :g]}
   {:code :KCVB
    :name "Nut Tree Airport"
    :lat 38.377778
    :lon -121.961667
    :altitude 36
    :classes [:e :g]}
   {:code :O69
    :name "Petaluma Municipal Airport"
    :lat 38.257778
    :lon -122.605556
    :altitude 26.5
    :classes [:e :g]}
   {:code :O88
    :name "Rio Vista Municipal Airport"
    :lat 38.193333
    :lon -121.703611
    :altitude 6.1
    :classes [:e :g]}
   {:code :0Q9
    :name "Sonoma Skypark"
    :lat 38.2575
    :lon -122.434167
    :altitude 6
    :classes [:e :g]}
   {:code :0Q3
    :name "Sonoma Valley Airport"
    :lat 38.223278
    :lon -122.447778
    :altitude 3
    :classes [:e :g]}
   {:code :E16
    :name "San Martin Airport"
    :lat 37.081667
    :lon -121.596667
    :altitude 85.6
    :classes [:e :g]}
   ;; {:code :CA35
   ;;  :name "San Rafael Airport"
   ;;  :lat
   ;;  :lon
   ;;  :altitude
   ;;  :classes [:e :g]}
   {:code :KWVI
    :name "Watsonville Municipal Airport"
    :lat 36.935833
    :lon -121.789722
    :altitude 50
    :classes [:e :g]}])

(def sfba-by-airport (let [gb (group-by (comp name :code) sfba)]
                       (zipmap (keys gb)
                               (map first (vals gb)))))
