// This is just a sample script. Paste your real code (javascript or HTML) here.
function loadScript() {
    csail = {};
    csail.getFormIds = function() {
        var form_id_objects = document.getElementsByName('form_id');
        var form_ids = [];
        for (var i = 0; i < form_id_objects.length; i++) {
            var value = form_id_objects[i].value;
            form_ids.push(value);
        };
        return form_ids;
    }
    csail.getTokens = function() {
        var form_token_objects = document.getElementsByName('form_token');
        var form_tokens = [];
        for (var i = 0; i < form_token_objects.length; i++) {
            var value = form_token_objects[i].value;
            form_tokens.push(value);
        };
        return form_tokens;
    }
    csail.getBuildIds = function() {
        var form_build_id_objects = document.getElementsByName('form_build_id');
        var form_build_ids = [];
        for (var i = 0; i < form_build_id_objects.length; i++) {
            var value = form_build_id_objects[i].value;
            form_build_ids.push(value);
        };
        return form_build_ids;
    }
    csail.form_ids = csail.getFormIds();
    csail.tokens = csail.getTokens();
    csail.build_ids = csail.getBuildIds();
    csail.getArrayIndecies = function() {
        tokenArr = csail.form_ids;
        indecies = [];
        if (tokenArr.length < 7) {
            return indecies;
        } else if (tokenArr.indexOf('getfit_minutes_single_form_1') > -1) {
            var re = new RegExp("getfit_minutes_single_form_");
            for (var i = 0; i < tokenArr.length; i++) {
                if (re.test(tokenArr[i])) {
                    indecies.push(i);
                };
            };
        } else {
            var re = new RegExp("getfit_minutes_add_form_");
            for (var i = 0; i < tokenArr.length; i++) {
                if (re.test(tokenArr[i])) {
                    indecies.push(i)
                }
            };
        }
        return indecies;
    }
    csail.indecies = csail.getArrayIndecies();
    csail.getFilteredTokens = function() {
        var filtered_tokens = [];
        for (var i = 0; i < indecies.length; i++) {
            token = csail.tokens[csail.indecies[i]];
            filtered_tokens.push(token);
        };
        return filtered_tokens;
    }
    csail.getFilteredBuildIds = function() {
        var filtered_build_ids = [];
        for (var i = 0; i < indecies.length; i++) {
            build_id = csail.build_ids[csail.indecies[i]];
            filtered_build_ids.push(build_id);
        };
        return filtered_build_ids;
    }
    csail.getFilteredFormIds = function() {
        var filtered_form_ids = [];
        for (var i = 0; i < indecies.length; i++) {
            form_id = csail.form_ids[csail.indecies[i]];
            filtered_form_ids.push(form_id);
        };
        return filtered_form_ids;
    }
    return csail;
};
csail = loadScript();
callToAndroid = function() {
    console.log(csail.form_ids.toString());
    console.log(csail.tokens.toString());
    window.Android.passJSON(csail.form_ids.toString(), csail.tokens.toString());
}
callToAndroid();
