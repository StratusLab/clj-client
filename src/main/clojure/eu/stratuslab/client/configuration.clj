(ns eu.stratuslab.client.configuration
  "Contains utility functions for dealing with the user 
   configuration file."
  (:require [clojure-ini.core :as ini]
            [clojure.java.io :as io]))

(def cfg-file-cwd
  (io/file (System/getProperty "user.dir")
           "stratuslab-user.cfg"))

(def cfg-file-home
  (io/file (System/getProperty "user.home")
           ".stratuslab"
           "stratuslab-user.cfg"))

(defn cfg-file 
  "Find the user configuration file to use.  The method first
   looks in the current working directory, then in the 
   .stratuslab directory within the user's home directory. 
   The filename is expected to be stratuslab-user.cfg."
  []
  (some #(if (.exists %) %) [cfg-file-cwd cfg-file-home]))

(defn read-cfg []
  "Read the StratusLab user configuration file as a map."
  (if-let [cfg-file (cfg-file)]
    (ini/read-ini (cfg-file)
                  :keywordize? true 
                  :comment-char \#)
    {}))

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

(def ^:dynamic *cfg* (cloud-cfg))

