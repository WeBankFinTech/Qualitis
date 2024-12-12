import { ref } from 'vue';

export default function useDateRange(startDate = 0, endDate = 0, days = 7) {
    const _startDate = ref(startDate);
    const _endDate = ref(endDate);
    const _days = ref(days);
    return {
        startDate: _startDate,
        endDate: _endDate,
        days: _days,
    };
}
