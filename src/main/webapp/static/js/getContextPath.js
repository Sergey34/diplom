function getContextPath() {
    let contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
    if (undefined == contextPath || contextPath == "/adminka") {
        return '';
    } else {
        return contextPath;
    }
}