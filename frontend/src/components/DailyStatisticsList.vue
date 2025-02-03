<template>
  <div>
    <h1>Daily Statistics</h1>

    <!-- Search input for filtering by date -->
    <input 
      type="date" 
      v-model="searchDate" 
      @change="searchStatistics" 
      placeholder="Search by date"
    />

    <!-- Back button to go back to paginated view after search -->
    <button v-if="searchDate" @click="backToPaginatedView">Back to Paginated View</button>

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
        <!-- When there is data, loop through statisticsList -->
        <tr v-for="(stat, index) in statisticsList" :key="index">
          <td>{{ stat.date }}</td>
          <td>{{ stat.statistics.totalConsumption }}</td>
          <td>{{ stat.statistics.totalProduction }}</td>
          <td>{{ stat.statistics.averagePrice }}</td>
          <td>{{ stat.statistics.longestNegativePriceDuration }}</td>
        </tr>
        <!-- If no data is found, show a "No Data" message -->
        <tr v-if="statisticsList.length === 0">
          <td colspan="5">No data found</td>
        </tr>
      </tbody>
    </table>

    <!-- Pagination controls for paginated data -->
    <div class="pagination" v-if="!searchDate">
      <button @click="changePage(page - 1)" :disabled="page <= 0">Previous</button>
      <span>Page {{ page + 1 }} of {{ totalPages }}</span>
      <button @click="changePage(page + 1)" :disabled="page >= totalPages - 1">Next</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'

export default {
  name: 'DailyStatisticsList',
  setup() {
    const searchDate = ref(''); // Holds the selected search date
    const statisticsList = ref([]); // Holds the statistics list (either paginated or searched)
    const page = ref(0); // Current page number
    const totalPages = ref(0); // Total number of pages

    // Function to fetch data based on search date or pagination
    const fetchStatistics = () => {
      const endpoint = searchDate.value
        ? `http://localhost:8080/api/electricity/daily-stats/${searchDate.value}` // Fetch by date if searchDate is selected
        : `http://localhost:8080/api/electricity/daily-stats/all?page=${page.value}&size=10`; // Fetch paginated data if no date is selected

      axios.get(endpoint)
        .then(response => {
          if (searchDate.value) {
            // For a specific date, wrap the statistics in an object with date
            statisticsList.value = [{
              date: searchDate.value, // Manually add the search date
              statistics: response.data, // The statistics returned from your backend
            }];
            totalPages.value = 1; // Only one result, so totalPages is 1
          } else {
            statisticsList.value = response.data.content; // Paginated data
            totalPages.value = response.data.totalPages; // Total number of pages
          }
        })
        .catch(error => {
          console.error("There was an error fetching the data:", error);
        });
    };

    // Fetch statistics on initial load
    onMounted(fetchStatistics);

    // Change page handler
    const changePage = (newPage) => {
      if (newPage >= 0 && newPage < totalPages.value) {
        page.value = newPage;
        fetchStatistics(); // Fetch data for the new page
      }
    };

    // Function to handle search by date
    const searchStatistics = () => {
      page.value = 0; // Reset to first page when searching
      fetchStatistics(); // Fetch data based on the search date
    };

    // Function to go back to paginated view
    const backToPaginatedView = () => {
      searchDate.value = ''; // Clear the search date
      page.value = 0; // Reset to the first page
      fetchStatistics(); // Fetch paginated data again
    };

    return {
      searchDate,
      statisticsList,
      page,
      totalPages,
      changePage,
      searchStatistics,
      backToPaginatedView,
    };
  }
};
</script>