var mReg = /^1[3|4|5|8|7][0-9]\d{8}$/ ;
var pReg = /^[1-9]{1}[0-9]*([.]{1}[0-9]{1,2})?$/;

function checkData(ele){
    var eVal = ele.val();
    var itemName = ele.parents('.form-group').find('label').text();
    if(!itemName){
        itemName = ele.attr('placeholder');
        if(!!itemName){
            itemName= itemName.replace('请输入','');
        }
    }

    var isEmpty = ele.data('empty') ; //如果可为空，则为true ; 如果为某个值时视为空，则传入此值
    if( !isEmpty && !eVal   ){
        alerted( itemName + "不能为空" , "shake");
        return false ;
    }

    if(!!isEmpty && eVal == isEmpty){
        alerted( "请选择" + itemName , "shake");
        return false ;
    }

    var minLen = ele.data('min');
    var maxLen = ele.data('max');
    if((!!minLen && eVal.length) < minLen || (!!maxLen && eVal.length > maxLen) ){
        var text = itemName + "的长度为" + minLen + "~" + maxLen + "个字符" ;
        if(minLen == maxLen) text = itemName + "输入错误" ;
        if(!minLen) text = itemName + "的最大长度为" + maxLen + "个字符" ;
        if(!maxLen) text = itemName + "的最小长度为" + minLen + "个字符" ;
        alerted( text , "shake");
        return false ;
    }

    var type = ele.data('type');
    if(isEmpty != true || !!eVal){
        if( (type == 'mobile' ) && !mReg.test(eVal)){
            alerted( itemName + "不正确" , "shake");
            return false ;
        }
        if( (type == 'id'  ) && !IdentityCodeValid(eVal)){
            alerted( itemName + "不正确" , "shake");
            return false ;
        }
        if( type == 'amount' && !pReg.test(eVal)){
            alerted( itemName + "不正确" , "shake");
            return false ;
        }
        if(type == 'int' && eVal.indexOf('.') != -1){
            alerted( itemName + "只允许输入整数" , "shake");
            return false ;
        }
    }

    return true ;
}

/*
 * 使用场景：ajax提交表单，为a添加data-submit="true"属性
 * 将所有需要提交的表单添加name，会自动收集以name为key的json数据
 * 默认数据保存成功后，刷新当前页面，如果需要跳转至其他页面，请为a添加data-redirect=""的路径，请忽略host
 */
var formResult = true ;
$('body').on('click', 'a[data-submit="true"]' , function(eve){
    eve.preventDefault();
    var _this = $(this);
    NP_component.loading(true) ;
    var form = _this.parents('form') ;
    var result = true ;
    var postJson ={};
    form.find(' select , input , textarea ').not('input[type="file"],input[type="checkbox"],input[type="radio"],input[type="hidden"]').each(function(i,e){
        var obj = $(this);
        if(!checkData(obj)){
            result = false ;
            return false ;
        }
        var name = obj.attr('name') ;
        if(!!name) postJson[name] = obj.val();
    }) ;
    if(!result) {
        NP_component.loading(false) ;
        return false ;
    }
    var addedCheck = _this.data('check');
    if(!!addedCheck){
        var checkFn = eval(addedCheck) ;
        new checkFn( _this , result ) ;
    }
    if(!formResult){
        NP_component.loading(false) ;
        return false ;
    }
    var vals = [] ;
    form.find('input[type="checkbox"]:checked ').each(function(i,e){
        var obj = $(this);
        var name = obj.attr('name') ;
        vals.push(obj.val());
        if(!!name) postJson[name] = vals.join(',');
    }) ;
    form.find('input[type="radio"]:checked, input[type="hidden"]').each(function(i,e){
        var obj = $(this);
        var name = obj.attr('name') ;
        if(!!name) postJson[name] = obj.val();
    }) ;
    var url = NP_config.service + _this.attr('href');
    var cmd = _this.data('cmd');
    var error = _this.data('error');
    var alertText = _this.data('alert');
    var direct = _this.data('redirect');
    $.post(url , postJson , function(result){
        NP_component.loading(false) ;
        var msg = result.status ;
        var tip = result.message ;
        if(msg == '200'){

            if(!!direct){
                var params = _this.data('params') || '' ;
                NP_component.switchPage({target:direct , params : params});
            }
            if(!!cmd){
                var cmds = eval(cmd);
                new cmds(_this,result);
            }
            if(!!alertText){
                alerted(alertText);
            }

        }else if(msg == '100'){
            alerted(tip);
        }else{
            if(!!error){
                var errors = eval(error);
                new errors(_this,result);
            }
            alerted(tip);
        }
    },'json');
});

$('body').on('click', 'a[data-send="true"]' , function(eve){
    eve.preventDefault();
    NP_component.loading(true);
    var _this = $(this);
    var url = NP_config.host + _this.attr('href');
    var callBack = _this.attr('data-success');
    var redirect = _this.attr('data-redirect') ;
    $.post(url ,  function(result){
        NP_component.loading(false);
        var status = result.status ;
        var tip = result.message ;
        if(status == "200"){
            if(!!callBack){
                var fn = eval(callBack);
                new fn(_this , result);
            }else if(!!redirect){
                window.location.href = host + redirect ;
            }else{
                NP_component.small(tip , 3000 , true) ;
            }
        }else if(status == "100"){
            alerted(tip);
        }else{
            alerted(tip);
        }
    },'json');
});

function alerted(t){
    alert(t);
}




