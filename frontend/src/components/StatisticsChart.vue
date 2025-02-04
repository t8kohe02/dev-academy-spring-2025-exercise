<template>
  <div>
    <div class="selector-container">
      <div class="month-selector">
        <button @click="changeMonth(-1)">◀--</button>
        <button @click="changeMonth(1)">--▶</button>
      </div>
      <span class="label">Daily averages for {{ formattedMonth }}</span>
    </div>

    <canvas ref="chartCanvas"></canvas>

    <p v-if="!isChartDataValid">No data available for the selected month.</p>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, computed, watch } from 'vue'
  import axios from 'axios'
  import {
    Chart,
    Title,
    Tooltip,
    Legend,
    BarElement,
    BarController,
    CategoryScale,
    LinearScale,
    LineElement,
    PointElement,
    LineController
  } from 'chart.js'

  Chart.register(
    Title,
    Tooltip,
    Legend,
    BarElement,
    BarController,
    CategoryScale,
    LinearScale,
    LineElement,
    LineController,
    PointElement
  )

  const statistics = ref<
    { date: string; totalConsumption: number; totalProduction: number; averagePrice: number }[]
  >([])
  const chartCanvas = ref<HTMLCanvasElement | null>(null)
  let chartInstance: Chart | null = null

  // Set default month to september 2024 for show purposes
  const selectedMonth = ref(new Date(2024, 8))

  const formattedMonth = computed(() => {
    return selectedMonth.value.toLocaleString('default', { month: 'long', year: 'numeric' })
  })

  const changeMonth = (offset: number) => {
    const newMonth = new Date(selectedMonth.value)
    newMonth.setMonth(newMonth.getMonth() + offset)
    selectedMonth.value = newMonth
  }

  const fetchStatistics = async () => {
    try {
      const month = selectedMonth.value.getMonth() + 1
      const year = selectedMonth.value.getFullYear()
      const monthString = `${year}-${month < 10 ? '0' + month : month}`

      console.log('Formatted month for API:', monthString)

      const response = await axios.get(
        `http://localhost:8080/api/electricity/chart/daily?month=${monthString}`
      )
      console.log('Fetched data:', response.data)
      statistics.value = response.data || []
      updateChart()
    } catch (error) {
      console.error('Error fetching data:', error)
    }
  }

  const isChartDataValid = computed(() => statistics.value.length > 0)

  const updateChart = () => {
    if (!chartCanvas.value) return

    if (chartInstance) {
      chartInstance.destroy()
    }

    if (!isChartDataValid.value) return

    const priceValues = statistics.value.map((stat) => stat.averagePrice)
    const minPrice = Math.min(...priceValues)
    const maxPrice = Math.max(...priceValues)

    chartInstance = new Chart(chartCanvas.value, {
      type: 'bar',
      data: {
        labels: statistics.value.map((stat) => {
          const date = new Date(stat.date)
          const day = String(date.getDate()).padStart(2, '0')
          const month = String(date.getMonth() + 1).padStart(2, '0')
          return `${day}/${month}`
        }),
        datasets: [
          {
            label: 'Total Consumption (MWh)',
            data: statistics.value.map((stat) => stat.totalConsumption / 1000),
            backgroundColor: 'rgba(54, 162, 235, 0.5)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          },
          {
            label: 'Total Production (MWh)',
            data: statistics.value.map((stat) => stat.totalProduction),
            backgroundColor: 'rgba(75, 192, 192, 0.5)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1
          },
          {
            label: 'Average Price (snt/kWh)',
            data: priceValues,
            borderColor: priceValues.map((value) =>
              value < 0 ? 'rgba(255, 0, 0, 1)' : 'rgba(255, 99, 132, 1)'
            ),
            backgroundColor: priceValues.map((value) =>
              value < 0 ? 'rgba(255, 0, 0, 0.5)' : 'rgba(255, 99, 132, 0.5)'
            ),
            borderWidth: 2,
            type: 'line',
            yAxisID: 'y-axis-price'
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            title: { display: true, text: 'Production & Consumption (MWh)' }
          },
          x: {
            reverse: true
          },
          'y-axis-price': {
            position: 'right',
            title: { display: true, text: 'Average Price (snt/kWh)' },
            max: maxPrice > 30 ? maxPrice + 5 : 30,
            min: minPrice < -10 ? minPrice - 5 : -10,
            grid: { drawOnChartArea: false }
          }
        }
      }
    })
  }

  onMounted(fetchStatistics)
  watch(selectedMonth, fetchStatistics)
</script>

<style scoped>
  .selector-container {
    display: flex;
    align-items: center;
    width: 100%;
    max-width: 400px;
    margin: 0 auto;
  }

  .month-selector {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-shrink: 0;
  }

  .label {
    white-space: nowrap;
    font-weight: bold;
    margin-left: 20px;
    flex-grow: 1;
  }
</style>
