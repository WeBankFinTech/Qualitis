export const colFormatter = ({
    cellValue,
}) => {
    if (!cellValue && cellValue !== 0) return '--';
};
export const datasourceTypeFormatter = ({
    cellValue,
}) => {
    const datasourceTypeMap = {
        1: 'HIVE',
        2: 'MYSQL',
        3: 'TDSQL',
        4: 'KAFKA',
        5: 'FPS',
    };
    return datasourceTypeMap[cellValue];
};
