<template>
  <div>
    <div class="input-container">
      <span class="search-label">Daily statistics </span>
      <div class="input-text">
        <span>Search: </span>
        <input
          type="date"
          v-model="searchDate"
          @change="searchStatistics"
          placeholder="Search by date"
        />
      </div>
    </div>

    <!-- Table to display fetched daily statistics -->
    <table border="1">
      <thead>
        <tr>
          <th>Date</th>
          <th>Total Consumption</th>
          <th>Total Production</th>
          <th>Average Price</th>
          <th>Longest Negative Price Duration</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(stat, index) in statisticsList" :key="index">
          <td>{{ formatDate(stat.date) }}</td>
          <td>{{ stat.statistics.totalConsumption }}</td>
          <td>{{ stat.statistics.totalProduction }}</td>
          <td>{{ stat.statistics.averagePrice }}</td>
          <td>{{ stat.statistics.longestNegativePriceDuration }}</td>
        </tr>
        <tr v-if="statisticsList.length === 0">
          <td colspan="5">No data found</td>
        </tr>
      </tbody>
    </table>

    <button v-if="searchDate" @click="backToPaginatedView">Return</button>

    <div class="pagination" v-if="!searchDate">
      <button @click="changePage(page - 1)" :disabled="page <= 0">◀--</button>
      <span>Page {{ page + 1 }} of {{ totalPages }}</span>
      <button @click="changePage(page + 1)" :disabled="page >= totalPages - 1">--▶</button>
    </div>
  </div>
</template>

<script>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'

  export default {
    name: 'DailyStatisticsList',
    setup() {
      const searchDate = ref('')
      const statisticsList = ref([])
      const page = ref(0)
      const totalPages = ref(0)

      const fetchStatistics = () => {
        const endpoint = searchDate.value
          ? `http://localhost:8080/api/electricity/daily-stats/${searchDate.value}` // Fetch by date if searchDate is selected
          : `http://localhost:8080/api/electricity/daily-stats/all?page=${page.value}&size=10` // Fetch paginated data if no date is selected

        axios
          .get(endpoint)
          .then((response) => {
            if (searchDate.value) {
              statisticsList.value = [
                {
                  date: searchDate.value,
                  statistics: response.data
                }
              ]
              totalPages.value = 1
            } else {
              statisticsList.value = response.data.content
              totalPages.value = response.data.totalPages
            }
          })
          .catch((error) => {
            console.error('There was an error fetching the data:', error)
          })
      }

      // Fetch statistics on initial load
      onMounted(fetchStatistics)

      const formatDate = (dateString) => {
        const date = new Date(dateString)
        const day = String(date.getDate()).padStart(2, '0')
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const year = date.getFullYear()

        return `${day}.${month}.${year}`
      }

      const changePage = (newPage) => {
        if (newPage >= 0 && newPage < totalPages.value) {
          page.value = newPage
          fetchStatistics()
        }
      }

      const searchStatistics = () => {
        page.value = 0
        fetchStatistics()
      }

      const backToPaginatedView = () => {
        searchDate.value = ''
        page.value = 0
        fetchStatistics()
      }

      return {
        searchDate,
        statisticsList,
        page,
        totalPages,
        changePage,
        searchStatistics,
        backToPaginatedView,
        formatDate
      }
    }
  }
</script>

<style scoped>
  .input-container {
    display: flex;
    align-items: center;
    justify-content: space-between; /* Ensures spacing between label and search */
    max-width: 800px; /* Adjust width as needed */
    margin: 0 auto 10px auto; /* Centers the container */
    gap: 50px; /* Large gap between label and search */
  }

  .search-container {
    display: flex;
    align-items: center;
    gap: 10px; /* Space between "Search:" and input */
  }

  .search-label {
    font-weight: bold;
    white-space: nowrap; /* Prevents wrapping */
    flex-shrink: 0; /* Prevents it from getting smaller */
  }

  .pagination {
    margin-top: 5px;
  }
</style>
