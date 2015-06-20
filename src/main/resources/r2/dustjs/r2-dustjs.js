/**
 * Dust 마크업으로 개발된 HTML 파일을 스크립트 형태의 템플릿 함수로 컴파일
 *
 * @param key
 * @param html
 * @returns {*}
 */
function dustCompile(key, html) {
    var compiled = dust.compile(html, key);
    return compiled;
}

/**
 *
 * Dust 함수로 컴파일된 HTML을 Dust 엔진에 로딩함
 *
 * @param template
 */
function dustLoad(template) {
    dust.loadSource(template);
}

/**
 * key로 로딩된 템플릿을 data를 매핑해 렌더링해 그 결과를 문자열 HTML 형태로 반환
 *
 * @param key
 * @param data
 */
function dustRender(key, json, successWriter) {
    dust.render(key, JSON.parse(json), function (err, data) {
        if (err) {
            throw new Error(err);
        } else {
            successWriter.write(data, 0, data.length);
        }
    });
}