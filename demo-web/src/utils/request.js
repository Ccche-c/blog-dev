import axios from 'axios'
import store from '@/store'
import { getUser, removeUser } from '@/utils/auth'
// create an axios instance
const service = axios.create({
    baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8080',
    timeout: 36000 // request timeout
})

// request interceptor
service.interceptors.request.use(
    config => {
        //do something before request is sent
        let user = getUser()
        if (user != null) {
            // let each request carry token
            // ['X-Token'] is a custom headers key
            // please modify it according to the actual situation
            config.headers.Authorization = user.token
        }

        return config
    },
    error => {
        // do something with request error
        console.log(error) // for debug
        return Promise.reject(error)
    }
)

// response interceptor
service.interceptors.response.use(
    /**
     * If you want to get http information such as headers or status
     * Please return  response => response
     */

    /**
     * Determine the request status by custom code
     * Here is just an example
     * You can also judge the status by HTTP Status Code
     */
    response => {
        const res = response.data
        // store.commit('SET_LOADING',false);
        // if the custom code is not 20000, it is judged as an error.
        if (res.code == 401) {
            removeUser()
        }
        if (res.code !== 200) {
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    error => {
        // 处理网络错误或响应错误
        if (error.response) {
            // 服务器返回了错误状态码
            console.error('响应错误:', error.response.status, error.response.data)
            // 如果后端返回了错误信息，使用后端的错误信息
            if (error.response.data && error.response.data.message) {
                error.message = error.response.data.message
            } else if (error.response.data && error.response.data.msg) {
                error.message = error.response.data.msg
            } else {
                error.message = `请求失败: ${error.response.status} ${error.response.statusText || ''}`
            }
        } else if (error.request) {
            // 请求已发出但没有收到响应（网络错误）
            console.error('网络错误 - 请求已发出但没有收到响应:', error.request)
            error.message = '网络错误，请检查网络连接或后端服务是否正常'
        } else {
            // 其他错误（如请求配置错误）
            console.error('请求配置错误:', error.message)
            error.message = error.message || '请求失败'
        }
        return Promise.reject(error)
    }
)

export default service