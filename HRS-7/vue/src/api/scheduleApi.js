import request from '@/utils/request'

export function addSchedule(data) {
    console.log(data)
    return request({
        url: '/api/schedule',
        method: 'post',
        data
    })
}

/*export function getSchedulesByHospital(hospitalId, startDate, endDate) {
    return request({
        url: `/api/schedule/hospital/${hospitalId}`,
        method: 'get',
        params: {
            startDate: startDate.toISOString().split('T')[0],
            endDate: endDate.toISOString().split('T')[0]
        }
    })
}*/

export function getSchedulesByDepartment(userId, departmentId, startDate, endDate) {
    return request({
        url: `/api/schedule/department/${departmentId}`,
        method: 'get',
        params: {
            userId,
            startDate: startDate.toISOString().split('T')[0],
            endDate: endDate.toISOString().split('T')[0]
        }
    })
}

export function deleteSchedule(id) {
    return request({
        url: `/api/schedule/${id}`,
        method: 'delete'
    })
}

export function updateSchedule(data) {
    return request({
        url: '/api/schedule',
        method: 'put',
        data
    })
}

export function getSchedulesByDoctor(doctorId, startDate, endDate) {
    return request({
        url: `/api/schedule/doctor/${doctorId}`,
        method: 'get',
        params: {
            startDate: startDate.toISOString().split('T')[0],
            endDate: endDate.toISOString().split('T')[0]
        }
    })
}