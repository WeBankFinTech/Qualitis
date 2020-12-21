function dateFormat(fmt, date = new Date()) {
    var o = {
        "M+": date.getMonth() + 1, //月份
        "d+": date.getDate(), //日
        "H+": date.getHours(), //小时
        "m+": date.getMinutes(), //分
        "s+": date.getSeconds(), //秒
        "q+": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k])
                .length)));
        }
    }
    return fmt;
}

/**
 * 获取人事接口提供的人员信息，并缓存
 */
function getStaff(ctx) {
    return new Promise(async (resolve, reject)=> {
        if(typeof ctx === 'undefined' || !ctx.FesApi){
            console.warn('ctx must not undefined');
            resolve([]);
            return;
        }
        let cacheStaff = ctx.FesApp.get("staffList");
        if (Array.isArray(cacheStaff)) {
            resolve(cacheStaff);
            return;
        }

        let staff = [];
        try {
            let result = await ctx.FesApi.fetch("api/v1/projector/staff", {}, "get");
            if (Array.isArray(result)) {
                staff = result.map((item) => {
                    let info = item.split(",");
                    return {
                        "username": info[0],
                        "chinese_name": info[1],
                        "full_name": `${info[0]}(${info[1]})`,
                        "org_name": info[2]
                    }
                });
                staff = _.uniqBy(staff,"username");
            }
            ctx.FesApp.set("staffList", staff);
        } catch (error) {
            console.error(error);
            reject(error);
        }
        resolve(staff);
    })
}


/*
 * Download--Excel
 * @param {blob} data
 * @param {文件名} fileName
 */
function forceDownload(blob, fileName) {
    const elink = document.createElement('a');
    elink.style.display = 'none';
    elink.download = fileName;
    elink.href = blob;
    elink.click();
}


/**
 *是否为IE浏览器
 */
function isIE() {
    if (!!window.ActiveXObject || "ActiveXObject" in window) {
        alert('请使用Chrome或其他高级浏览器，IE可能会无法正常显示');
        return true;
    }
}

function getUserRole(params) {
    params.FesApi.fetch("api/v1/projector/role", "get").then(({roles,username}) => {
        if(Array.isArray(roles)){
            roles = roles.map(item => item.toLowerCase())
        }
        let role = 'noauth';
        if(roles && roles.indexOf('admin') > -1 ){
            role = 'admin';
            params.FesApp.set("FesRoleName", "管理员");
        }
        params.FesApp.setRole(role);
        if (!params.FesStorage.get('simulatedUser')) {
            params.FesApp.set("FesUserName", username);
        }
        params.$router.push({ path: "/dashboard" });
    }).catch(() => {
        let role = params.FesStorage.get('firstRole');
        if (role) {
            params.FesApp.setRole(role);
        }else {
            params.FesApp.setRole('noauth');
        }
    });
}

function DWSMessage(key, ruleGroupId, action) {
    const jsonStr = {
        type: 'qualitis',
        nodeId: key,
        data: {
            ruleGroupId,
            action
        }
    }
    window.parent.postMessage(JSON.stringify(jsonStr), '*')
}

function sha256(s) {
    var chrsz = 8;
    var hexcase = 0;
    function safe_add(x, y) {
        var lsw = (x & 0xffff) + (y & 0xffff);
        var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
        return (msw << 16) | (lsw & 0xffff);
    }
    function S(X, n) {
        return (X >>> n) | (X << (32 - n));
    }
    function R(X, n) {
        return X >>> n;
    }
    function Ch(x, y, z) {
        return (x & y) ^ (~x & z);
    }
    function Maj(x, y, z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }
    function Sigma0256(x) {
        return S(x, 2) ^ S(x, 13) ^ S(x, 22);
    }
    function Sigma1256(x) {
        return S(x, 6) ^ S(x, 11) ^ S(x, 25);
    }
    function Gamma0256(x) {
        return S(x, 7) ^ S(x, 18) ^ R(x, 3);
    }
    function Gamma1256(x) {
        return S(x, 17) ^ S(x, 19) ^ R(x, 10);
    }
    function core_sha256(m, l) {
        var K = new Array(
            0x428a2f98,
            0x71374491,
            0xb5c0fbcf,
            0xe9b5dba5,
            0x3956c25b,
            0x59f111f1,
            0x923f82a4,
            0xab1c5ed5,
            0xd807aa98,
            0x12835b01,
            0x243185be,
            0x550c7dc3,
            0x72be5d74,
            0x80deb1fe,
            0x9bdc06a7,
            0xc19bf174,
            0xe49b69c1,
            0xefbe4786,
            0xfc19dc6,
            0x240ca1cc,
            0x2de92c6f,
            0x4a7484aa,
            0x5cb0a9dc,
            0x76f988da,
            0x983e5152,
            0xa831c66d,
            0xb00327c8,
            0xbf597fc7,
            0xc6e00bf3,
            0xd5a79147,
            0x6ca6351,
            0x14292967,
            0x27b70a85,
            0x2e1b2138,
            0x4d2c6dfc,
            0x53380d13,
            0x650a7354,
            0x766a0abb,
            0x81c2c92e,
            0x92722c85,
            0xa2bfe8a1,
            0xa81a664b,
            0xc24b8b70,
            0xc76c51a3,
            0xd192e819,
            0xd6990624,
            0xf40e3585,
            0x106aa070,
            0x19a4c116,
            0x1e376c08,
            0x2748774c,
            0x34b0bcb5,
            0x391c0cb3,
            0x4ed8aa4a,
            0x5b9cca4f,
            0x682e6ff3,
            0x748f82ee,
            0x78a5636f,
            0x84c87814,
            0x8cc70208,
            0x90befffa,
            0xa4506ceb,
            0xbef9a3f7,
            0xc67178f2
        );
        var HASH = new Array(
            0x6a09e667,
            0xbb67ae85,
            0x3c6ef372,
            0xa54ff53a,
            0x510e527f,
            0x9b05688c,
            0x1f83d9ab,
            0x5be0cd19
        );
        var W = new Array(64);
        var a, b, c, d, e, f, g, h, i, j;
        var T1, T2;
        m[l >> 5] |= 0x80 << (24 - (l % 32));
        m[(((l + 64) >> 9) << 4) + 15] = l;
        for (i = 0; i < m.length; i += 16) {
            a = HASH[0];
            b = HASH[1];
            c = HASH[2];
            d = HASH[3];
            e = HASH[4];
            f = HASH[5];
            g = HASH[6];
            h = HASH[7];
            for (j = 0; j < 64; j++) {
                if (j < 16) W[j] = m[j + i];
                else
                    W[j] = safe_add(
                        safe_add(
                            safe_add(Gamma1256(W[j - 2]), W[j - 7]),
                            Gamma0256(W[j - 15])
                        ),
                        W[j - 16]
                    );
                T1 = safe_add(
                    safe_add(
                        safe_add(
                            safe_add(h, Sigma1256(e)),
                            Ch(e, f, g)
                        ),
                        K[j]
                    ),
                    W[j]
                );
                T2 = safe_add(Sigma0256(a), Maj(a, b, c));
                h = g;
                g = f;
                f = e;
                e = safe_add(d, T1);
                d = c;
                c = b;
                b = a;
                a = safe_add(T1, T2);
            }
            HASH[0] = safe_add(a, HASH[0]);
            HASH[1] = safe_add(b, HASH[1]);
            HASH[2] = safe_add(c, HASH[2]);
            HASH[3] = safe_add(d, HASH[3]);
            HASH[4] = safe_add(e, HASH[4]);
            HASH[5] = safe_add(f, HASH[5]);
            HASH[6] = safe_add(g, HASH[6]);
            HASH[7] = safe_add(h, HASH[7]);
        }
        return HASH;
    }
    function str2binb(str) {
        var bin = Array();
        var mask = (1 << chrsz) - 1;
        for (var i = 0; i < str.length * chrsz; i += chrsz) {
            bin[i >> 5] |=
                (str.charCodeAt(i / chrsz) & mask) << (24 - (i % 32));
        }
        return bin;
    }
    function Utf8Encode(string) {
        string = string.replace(/\r\n/g, "\n");
        var utftext = "";
        for (var n = 0; n < string.length; n++) {
            var c = string.charCodeAt(n);
            if (c < 128) {
                utftext += String.fromCharCode(c);
            } else if (c > 127 && c < 2048) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            } else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }
        }
        return utftext;
    }
    function binb2hex(binarray) {
        var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
        var str = "";
        for (var i = 0; i < binarray.length * 4; i++) {
            str +=
                hex_tab.charAt(
                    (binarray[i >> 2] >> ((3 - (i % 4)) * 8 + 4)) & 0xf
                ) +
                hex_tab.charAt(
                    (binarray[i >> 2] >> ((3 - (i % 4)) * 8)) & 0xf
                );
        }
        return str;
    }
    s = Utf8Encode(s);
    return binb2hex(core_sha256(str2binb(s), s.length * chrsz));
}
export {
    dateFormat,
    getStaff,
    forceDownload,
    isIE,
    getUserRole,
    DWSMessage,
    sha256
}
