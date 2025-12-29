import Cookies from 'js-cookie'

const userKey = 'user'


export function getUser() {
    let value = Cookies.get(userKey)
    if (value != null) {
        return JSON.parse(value)
    }
    return value
}

export function setUser(value) {
    return Cookies.set(userKey, value, { expires: 1 / 24 });
}
export function removeUser() {
    return Cookies.remove(userKey)
}
export function setSkin(value) {
    return window.sessionStorage.setItem("skin", value)
}
export function getSkin() {
    return window.sessionStorage.getItem("skin")
}
export function setUrl(value) {
    return window.sessionStorage.setItem("baseUrl", value)
}
export function getUrl() {
    return window.sessionStorage.getItem("baseUrl")
}
export function hasAuth(perms, perm) {
    let hasA = false
    perms.forEach(p => {
        if (p.indexOf(perm) !== -1) {
            hasA = true
            return false
        }
    })
    return hasA
}
