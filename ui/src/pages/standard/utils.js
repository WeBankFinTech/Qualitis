export const getActionRangeString = arr => arr.join('、') || '--';
// export const getDepartmentName = arr => arr && typeof arr?.join === 'function' && arr?.join(' ');
export const getDepartmentName = arr => (Array.isArray(arr) ? arr.join('/') : arr);
