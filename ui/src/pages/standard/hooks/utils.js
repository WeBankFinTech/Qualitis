export const utils = () => {
    const findValue = (array, value, valueField, findField) => array.find(v => v[valueField] === value)?.[findField] || '';
    return {
        findValue,
    };
};
