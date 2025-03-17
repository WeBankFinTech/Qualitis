import { ref } from 'vue';

export default function useDateRange(startMonth = 0, endMonths = 0, months = 7) {
    const _startMonth = ref(startMonth);
    const _endMonth = ref(endMonths);
    const _months = ref(months);
    return {
        startMonth: _startMonth,
        endMonth: _endMonth,
        months: _months,
    };
}
