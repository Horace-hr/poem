function uploadNoWater(eleID,ctx , fn ){
    Qiniu.uploader({
        runtimes: 'html5',
        browse_button: eleID,
        max_file_size: '5mb',
        uptoken_url: ctx + '/qiniu',
        domain:'http://onxf6uo7h.bkt.clouddn.com/',
        save_key: true,
        auto_start: true,
        init: {
            'FileUploaded': function(up, file, info) {
                cancelProgress();
                var curDomain = up.getOption('domain');
                var key = JSON.parse(info).key;
                var imgLink = curDomain + key ;
                if($.isFunction(fn)){
                    fn( imgLink );
                }
            },
            'Error': function(up, err, errTip) {
                alerted(errTip);
            },
            'UploadProgress': function(up, file) {
                setProgress(file);
            }
        }
    });
}

function setProgress(file){
    var progress = $('#progress');
    if(progress.length == 0){
        progress = $('<div id="progress" class="action-wrapper action-center"></div>') ;
        $('body').append(progress);
    }else{
        progress.show();
    }
    progress.html( '<div class="text-center">' +  file.percent + '%</div>' ) ;
}

function cancelProgress(){
    $('#progress').hide();
}

