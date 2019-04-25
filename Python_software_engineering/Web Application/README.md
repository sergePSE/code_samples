# Attendance application web server

The web server of the attendance application is available under: https://ase62-attendance-app.appspot.com/

## API endpoints

The endpoints of the web application:

| REST method  | Path | Description  |
| ------------- | ------------- | ------------- |
| GET  | /rest/attendances/  | Returns the XML representation of the attendance log (all attendances)  |
| GET  | /rest/attendances/{groupId}/{attendanceId}  | Returns the XML of the attendance item with the specified ID  |
| GET  | /rest/tokens/{studentId}/{weekId}  | Returns the value of the token, if found  |
| GET  | /groupFinder?studentId={}&tutor_name={}  | Returns the group id if found the match  |
| POST  | /rest/attendances/  | Posts an attendance record, validates it and, if valid, persists in the database. The attendance record is represented with XML in the body of the request.  |

## Attendance record structure

An example for the XML representation of an attendance record:

```xml
<attendance>
   <attendance_id>12345</attendance_id>
   <student_id>185804764220139124118</student_id>
   <group_id>6192449487634432</group_id>
   <week_id>0</week_id>
   <presented>true</presented>
</attendance>
```

The <b>attendance ID</b> is equivalent to the token of a student. Tokens are randomly generated in the range 10000 to 99999.
<br/>
The <b>week ID</b> represents the number of the tutorial session. In total there are 13 tutorial sessions and the ID ranges from 0 to 12.