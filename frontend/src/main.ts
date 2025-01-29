import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import axios from 'axios'

const app = createApp(App)

const API_URL = "http://localhost:8080/api"

const fetchData = async () => {
    try {
        const response = await axios.get(`${API_URL}/hello`)
        console.log("Backend response:", response.data)
    } catch (error) {
        console.error("Error fetching data:", error)
    }
}

app.mount('#app')
fetchData()
