
(ns client.render
  (:require
    [respo.alias :refer [html head title script style meta' div link body]]
    [respo.render.html :refer [make-html make-string]]
    [client.comp.container :refer [comp-container]]
    ["fs" :refer (readFileSync writeFileSync)]))

(def fs (js/require "fs"))

(defn slurp [x]
  (readFileSync x "utf8"))

(defn spit [file-name content]
  (writeFileSync file-name content)
  (println "Wrote to:" file-name))

(defn html-dsl [config resources html-content]
  (make-html
    (html {}
      (head {}
        (title {:attrs {:innerHTML "Stack Workflow"}})
        (link {:attrs {:rel "icon" :type "image/png" :href "http://logo.mvc-works.org/mvc.png"}})
        (link (:attrs {:rel "manifest" :href "manifest.json"})
        (meta' {:attrs {:charset "utf-8"}})
        (meta' {:attrs {:name "viewport" :content "width=device-width, initial-scale=1"}})
        (meta' {:attrs {:id "config" :type "text/edn" :content (pr-str config)}}))
        (if (contains? resources :css)
          (link {:attrs {:rel "stylesheet" :type "text/css" :href (:css resources)}})))
      (body {}
        (div {:attrs {:id "app" :innerHTML html-content}})
        (if (:build? config)
          (script {:attrs {:src (:vendor resources)}})
          (script {:attrs {:src (:dev resources)}}))
        (script {:attrs {:src (:main resources)}})))))

(defn generate-html []
  (let [ tree (comp-container {} #{:shell})
         html-content (make-string tree)
         resources (let [manifest (js/JSON.parse (slurp "dist/manifest.json"))]
                      {:css (aget manifest "main.css")
                       :main (aget manifest "main.js")
                       :vendor (aget manifest "vendor.js")})]
    (html-dsl {:build? true} resources html-content)))

(defn generate-empty-html []
  (html-dsl {:build? false}
    {:main "http://localhost:8080/main.js"
     :dev "http://localhost:8080/main-dev.js"}
    ""))

(defn -main []
  (spit "dist/index.html"
    (if (= js/process.env.env "dev")
      (generate-empty-html)
      (generate-html))))

(-main)
