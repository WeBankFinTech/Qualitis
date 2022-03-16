export const RuleMixin = {
    data() {
        return {
            maxSize: 2147483647
        };
    },
    methods: {
        getColumnNameWithDataType(column) {
            if (!column || typeof column !== 'object') return;
            const dataType = column.data_type;
            const columnName = column.column_name;
            return dataType ? `${columnName} (${dataType})` : columnName;
        },
        getDataSources(clusterName, proxyUser = '') {
            if (!clusterName) return Promise.resolve([]);
            return new Promise((resolve) => {
                this.FesApi.fetch('/api/v1/projector/meta_data/data_source/info', {
                    clusterName,
                    proxyUser,
                    name: '',
                    typeId: '',
                    currentPage: 1,
                    pageSize: this.maxSize
                }, 'get').then((res) => {
                    if (!Array.isArray(res.query_list)) resolve([]);
                    resolve(res.query_list);
                }).catch((error) => {
                    console.error(error);
                    resolve([]);
                });
            });
        },
    }
};
