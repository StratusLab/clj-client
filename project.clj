(defproject cloud-client "0.1-SNAPSHOT"
  :description "simple StratusLab cloud client in clojure"
  :url "https://github.com/loomis/cloud-client"
  :license {:name "Apache 2"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :aot [eu.stratuslab.client.compute
        eu.stratuslab.client.storage
        eu.stratuslab.client.configuration
        eu.stratuslab.client.start]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clojure-ini "0.0.1"]
                 [clj-http "0.4.1"]
                 [necessary-evil "1.2.2"]
                 [lein-eclipse "1.0.0"]])



