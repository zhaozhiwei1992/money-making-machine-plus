import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [uni()],
  server: {
    port: 5173,
    proxy: {
      // 选项写法
      '/api': {
        target: 'http://localhost:8090',
        changeOrigin: true,
        // 后端接口也有/api开头, 所以不要去掉了
        // rewrite: path => path.replace(/^\/api/, '')
      }
    },
    // hmr: {
    //   overlay: false
    // },
    hmr: true,
    host: '0.0.0.0'
  },
});
