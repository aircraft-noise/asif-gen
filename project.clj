;; (require 'cemerick.pomegranate.aether)
;; (cemerick.pomegranate.aether/register-wagon-factory!
;;  "http" #(org.apache.maven.wagon.providers.http.HttpWagon.))

(defproject org.aircraft-noise/analyze

  "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/data.xml "0.2.0-alpha5"]
                 [cheshire "5.8.0"]
                 [clj-time "0.14.3"]
                 [slingshot "0.12.2"]]

  ;; :jvm-opts ["--add-modules" "java.xml.bind"]

  :plugins [[lein-codox "0.10.1"]]

  :codox {:output-path "resources/doc/api"}

  :aliases {
            "generate-asif"              ["run" "-m" "analyze.job/generate-file"]
            "generate-asif-both"         ["run" "-m" "analyze.job/generate-file"]
            "generate-asif-departures"   ["run" "-m" "analyze.job/generate-departures-file"]
            "generate-asif-arrivals"     ["run" "-m" "analyze.job/generate-arrivals-file"]
            }
  )
