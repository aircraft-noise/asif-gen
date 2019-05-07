(defproject org.aircraft-noise/asif-gen

  "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/data.xml "0.2.0-alpha5"]
                 [cheshire "5.8.0"]
                 [clj-time "0.14.4"]
                 [slingshot "0.12.2"]
                 [me.raynes/fs "1.4.6"]
                 [circleci/clj-yaml "0.5.6"]
                 [org.clojure/tools.cli "0.3.7"]]

  ;; :jvm-opts ["--add-modules" "java.xml.bind"]

  :plugins [[lein-codox "0.10.1"]]

  :main analyze.job

  :bin {:name "asif-gen"
        :bin-path "./bin"}

  :codox {:output-path "resources/doc/api"}

  :aliases {
            "asif-gen"   ["run" "-m" "analyze.job/process-study"]
            "tcr2edn"    ["run" "-m" "analyze.job/tcr-edn"]
            }
  )
