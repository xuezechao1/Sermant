import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

import './assets/main.css'

const app = createApp(App)
app.directive("scrollBottom", {
  updated(el) {
   // 这里的el即是绑定指令处的dom元素
    el.scrollTo({
      top: el.scrollHeight - el.clientHeight,
      behavior: "smooth"
    })
  }
})

app.use(createPinia())
app.use(ElementPlus)
app.use(router)

app.mount('#app')
