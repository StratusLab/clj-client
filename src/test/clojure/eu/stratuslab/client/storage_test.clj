(ns eu.stratuslab.client.storage-test
  (:use clojure.test
        eu.stratuslab.client.storage
        [clojure.string :only (blank?)]))

(deftest test-empty-storage-command
  (let [x (storage)]
    (is (not (blank? x)))))

