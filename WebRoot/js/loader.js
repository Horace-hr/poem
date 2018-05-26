window.onload = function () {
    function loadJsFile(url) {
        var script = document.createElement("script");
        script.type = "text/javascript";
        script.src = url ;
        document.body.appendChild(script);
        return script ;
    }

    function loadJsFiles(fileNames) {
        var curScript ;
        for (var i = 0; i < fileNames.length; i++) {
            curScript = loadJsFile(fileNames[i]);
        }
    }

    loadJsFile('http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js').onload = function () {
        loadJsFiles([
            '/js/niep.base.js' ,
            '/js/niep.js' ,
            '/js/niep-method.js?v=11' ,
            '/js/form.js' ,
            '/js/main.js?v=122111111'
          //  '/js/niep-method.min.js?v=20170303'
        ]);
    } ;
    
   /* loadJsFile('http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js').onload = function () {
        loadJsFiles([
            '/js/niep.base.js' ,
            '/js/niep.js' ,
            '/js/niep-method.min.js?v=11' ,
         //   '/js/form.js' ,
            '/js/main.js?v=122111111'
          //  '/js/niep-method.min.js?v=20170303'
        ]);
    } ;*/
    
    
} ;