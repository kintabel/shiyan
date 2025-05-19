// src/utils/date.js
export function getCurrentWeekDates(baseDate = new Date()) {
    const date = new Date(baseDate)
    const day = date.getDay()
    const diff = date.getDate() - day + (day === 0 ? -6 : 1) // 调整为周一
    const monday = new Date(date.setDate(diff))

    return Array.from({ length: 7 }).map((_, i) => {
        const d = new Date(monday)
        d.setDate(d.getDate() + i)
        return formatDate(d, 'yyyy-MM-dd')
    })
}

export function formatDate(date, fmt = 'yyyy-MM-dd') {
    date = new Date(date)
    const o = {
        'M+': date.getMonth() + 1,
        'd+': date.getDate(),
        'h+': date.getHours(),
        'm+': date.getMinutes(),
        's+': date.getSeconds(),
        'q+': Math.floor((date.getMonth() + 3) / 3),
        S: date.getMilliseconds()
    }

    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(
            RegExp.$1,
            (date.getFullYear() + '').substr(4 - RegExp.$1.length)
        )
    }

    for (const k in o) {
        if (new RegExp(`(${k})`).test(fmt)) {
            fmt = fmt.replace(
                RegExp.$1,
                RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
            )
        }
    }

    return fmt
}