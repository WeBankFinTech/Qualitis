export function repeatValidator(n, arr) {
    return new Promise((res, rej) => {
        const names = arr.map(({ value, name }) => name);
        const set = new Set(
            names,
        );
        if (set.size === names.length) {
            res();
        } else {
            rej(`${n} key值有重复`);
        }
    });
}
