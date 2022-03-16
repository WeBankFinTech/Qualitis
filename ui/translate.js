var fs = require('fs');
var http = require('http');
var jsstr = fs.readFileSync('./cn.json', 'utf-8');
var json = JSON.parse(jsstr);

// http://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=%E8%AE%A1%E7%AE%97

// {
//     type: "ZH_CN2EN",
//     errorCode: 0,
//     elapsedTime: 1,
//     translateResult: [
//         [
//             {
//             src: "计算",
//             tgt: "To calculate"
//             }
//         ]
//     ]
// }

var url = 'http://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=';

function translateReq(text, cb) {
    http.get(`${url}${encodeURIComponent(text)}`, {
            headers:{
                'host': 'fanyi.youdao.com',
                'pragma': 'no-cache',
                'proxy-Connection': 'keep-alive',
                'upgrade-insecure-requests': 1,
                'cookie': 'OUTFOX_SEARCH_USER_ID=-1618593213@10.168.8.76; OUTFOX_SEARCH_USER_ID_NCOO=1383392327.7494588; _ga=GA1.2.1485522253.1539140851; P_INFO=15109269782|1545711281|1|youdaonote|00&99|null&null&null#gud&440300#10#0|&0||15109269782; SESSION_FROM_COOKIE=www.baidu.com',
                'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36'
            }
    },
    function (res) {
        var str='';
        res.on("data", (data) => {
            str += data;
        })
        res.on("end", () => {
            var json = JSON.parse(str)
            cb(json.translateResult[0] || [] )
        })

    })
    .on('error', function () {
        cb()
    })
}

var keys = Object.keys(json);
var start = 0;

function translate() {
    if (start < keys.length) {
        translateReq(json[keys[start]], function (v) {
            if (v[0]) {
                json[keys[start]] = v[0].tgt;
            }
            start = start + 1;
            translate(start)
        })
    } else {
        var jsonResult = {
            cn: JSON.parse(jsstr),
            en: json
        }
        fs.writeFile('./cn.json', JSON.stringify(jsonResult), function(err) {
            if (err) {
                throw err;
            }
            console.log('done.');
        })
    }
}

translate()
