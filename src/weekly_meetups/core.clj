(ns weekly-meetups.core
  (:require [clojure.data.json :as json])
  (require [again.core :as again])
  (:use [clj-time.core]
        [clj-time.coerce]
        [clj-time.format]
        [clj-time.local]
        [clostache.parser])
  (:gen-class))

;;configuration
(def number-of-days 30)

(def meetups
  {:brisbane []
   :tst []
   :sydney []
   :perth []
   :melbourne [
    "Female-Coders-Lab-Melbourne"
    "Melbourne-Scala-User-Group"
    "golang-mel"
    "Infrastructure-Coders"
    "Melbourne-Haskell-Users-Group"
    "devops-melbourne"
    "AWS-AUS"
    "MelbourneMUG"
    "Docker-Melbourne-Australia"
    "AngularJS-Melbourne"
    "Meteor-Melbourne"
    "gdg-melbourne"
    "Melbourne-Python-Meetup-Group"
    "Swift-Devs-Melbourne"
    "Melbourne-Microservices"
    "MelbCSS"
    "Melbourne-Apache-Spark-Meetup"
    "React-Melbourne"
    "scrum-12"
    "Agile-Project-Managers-Melbourne"
    "Agile-Business-Analysts-Melbourne"
    "OpenTechSchool-Melbourne"
    "The-UX-Design-Group-of-Melbourne"
    "Open-Knowledge-Melbourne"
    "Application-Security-OWASP-Melbourne"
    "Big-Data-Analytics-Meetup-Group"
    "HadoopMelbourne"
    "Ruby-On-Rails-Oceania-Melbourne"
    "hack-for-privacy"
    "BuzzConf"
    "Melbourne-Java-JVM-Users-Group"
    "JD-Junior-Developers-Melbourne"
    "AgileCoach"
    "Limited-WIP-Society"
    "Melbourne-Lean-Change-Management-Meetup"
    "Cynefin-Melbourne-Meetup-Group"
    "Responsive-Org-Melbourne"
    "CTO-School-Melbourne"
    "Product-Anonymous-Meetup-Melbourne"
    "ProductTank-Melbourne"
    "Melbourne-Lean-Coffee"
    "Visual-Practitioners-Melbourne"
    "Design-Thinking-and-Business-Innovation-Melbourne"
    "Melbourne-UX-Leadership-Meetup"
    "SecTalks-Melbourne"
    "Melbourne-VR"
    "StartupVictoria"
    "The-UX-Design-Group-of-Melbourne"
    "Melbourne-CocoaHeads"
    "PyLadies-Melbourne"
    "Melbourne-Functional-User-Group-MFUG"
    "ThoughtWorks-Melbourne"
    "Computer-Graphics-on-the-Web"
    "Melbourne-Blender-Society"
    "Machine-Learning-AI-Meetup"
    "Data-Engineering-Melbourne"
    "melbnlp"
    "Melbourne-Women-in-Machine-Learning-and-Data-Science"
    "Melbourne-DevSecOps-User-Group"
    "Melbourne-Kubernetes-Meetup"
    "GDG-Cloud-Melbourne"
    "Melbourne-Creative-AI-Meetup"
    "melbourne-search"
    "codelikeagirlau"
    "Women-Who-Code-Melbourne"
    "Disruptors-In-Tech-Melb"
    "DDD-Melbourne-By-Night"
    "Elm-Melbourne"
    "Melbourne-ML-AI-Bookclub"
    "Melbourne-Kotlin-Meetup"
    "Junior-Developers-Melbourne"
    "Melbourne-Docker-User-Group"
    "the-web"
   ]
})

;;should need to change this
(def meetup-url
  "http://api.meetup.com/2/events?sign=true&key=%s&group_urlname=%s")

(def events-template
  "events.mustache")

(def output-file
  "output.html")

;;hacky code that does stuff
(defn- get-meetup-events [api-key meetup]
  (println "get " meetup)
  (Thread/sleep 5000)
  (again/with-retries
    [100 1000 10000]
		(-> (format meetup-url api-key meetup number-of-days)
				slurp
				(json/read-str :key-fn keyword)
				:results)))

(defn- get-all-meetups [api-key city]
  (->> (map #(get-meetup-events api-key %) (city meetups))
       flatten))

(defn- format-time [time]
   (unparse
    (with-zone (formatter "yyyy-MM-dd EEEE") (default-time-zone))
    (from-long time)))

(defn- format-event [event]
  {:name (:name event)
   :group_name (:name (:group event))
   :time (format-time (:time event))
   :url (:event_url event)
   :venue_name (:name (:venue event))})

(defn- group-events [events]
	(def g (for [[k v] (group-by #(get % :time) events)] {:time k :values v}))
	g)


(defn events-to-html [events]
  (render-resource events-template {:events (sort-by :time (group-events events))}))

(defn get-events [api-key city]
  (->>(get-all-meetups api-key city)
      (sort-by :time)
      (map format-event)))

(defn -main
  ([api-key]
     (-main api-key "melbourne" output-file))
  ([api-key city]
     (-main api-key city output-file))
  ([api-key city output-file-name]
     (->> (get-events api-key (keyword city))
          events-to-html
          (spit output-file-name))))
