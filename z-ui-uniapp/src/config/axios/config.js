const config = {
    server_url: 'http://localhost:8080',
    /**
     * api请求基础路径
     */
    base_url: {
        // 开发环境接口前缀
        base: '/api',
        // 打包开发环境接口前缀
        dev: '',
        // 打包生产环境接口前缀
        pro: '',
        // 打包测试环境接口前缀
        test: ''
    },
    /**
     * 接口成功返回状态码
     */
    result_code: '200',
    /**
     * 接口请求超时时间
     */
    request_timeout: 60000,
    /**
     * 默认接口请求类型
     * 可选值：application/x-www-form-urlencoded multipart/form-data
     */
    default_headers: 'application/json'
};

export default config;