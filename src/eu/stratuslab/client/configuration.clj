(ns eu.stratuslab.client.configuration
  "Contains utility functions for dealing with the user 
   configuration file."
  (:require [clojure-ini.core :as ini]
            [clojure.java.io :as io]))

(defn cfg-file 
  "Find the user configuration file to use.  The method first
   looks in the current working directory, then in the 
   .stratuslab directory within the user's home directory. 
   The filename is expected to be stratuslab-user.cfg."
  []
  (first 
    (filter #(.exists %)
            [(io/file (System/getProperty "user.dir")
                      "stratuslab-user.cfg")
             (io/file (System/getProperty "user.home")
                      ".stratuslab"
                      "stratuslab-user.cfg")])))

(defn read-cfg []
  "Read the StratusLab user configuration file as a map."
  (ini/read-ini (cfg-file)
            :keywordize? true 
            :comment-char \#))

(defn cloud-cfg 
  "Provide merged map of cloud configuration parameters for
   the named cloud (as a keyword).  If no cloud name is given,
   then the default cloud will be used or just the defaults 
   section if no default cloud section is defined."
  [& ks]
  (let [cfg (read-cfg)
        user-defaults (get-in cfg [:default])]
    (if-let [k (or (first ks) (:selected_section user-defaults))]
      (merge user-defaults (get-in cfg [k]))
      user-defaults)))
