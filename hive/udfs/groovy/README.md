# Groovy UDF example
#### Can be compiled at run time
##### Currently only works in "hive" shell, does not work in beeline

```
su guest
hive
```

##### paste the following code into the hive shell
##### this will use Groovy String replace function to replace all instances of lower case 'e' with 'E'

```
compile `import org.apache.hadoop.hive.ql.exec.UDF \;
import org.apache.hadoop.io.Text \;
public class Replace extends UDF {
  public Text evaluate(Text s){
    if (s == null) return null \; 
	return new Text(s.toString().replace('e', 'E')) \;
  }
} ` AS GROOVY NAMED Replace.groovy;

```

##### now create a temporary function to leverage the Groovy UDF

```
CREATE TEMPORARY FUNCTION Replace as 'Replace';
```

##### now you can use the function in your SQL

```
SELECT Replace(description) FROM sample_08 limit 5;
```

```
hive> compile `import org.apache.hadoop.hive.ql.exec.UDF \;
    > import org.apache.hadoop.io.Text \;
    > public class Replace extends UDF {
    >   public Text evaluate(Text s){
    >     if (s == null) return null \;
    >     return new Text(s.toString().replace('e', 'E')) \;
    >   }
    > } ` AS GROOVY NAMED Replace.groovy;
Added [/tmp/0_1452022176763.jar] to class path
Added resources: [/tmp/0_1452022176763.jar]
hive> CREATE TEMPORARY FUNCTION Replace as 'Replace';
OK
Time taken: 1.201 seconds
hive> SELECT Replace(description) FROM sample_08 limit 5;
OK
All Occupations
ManagEmEnt occupations
ChiEf ExEcutivEs
GEnEral and opErations managErs
LEgislators
Time taken: 6.373 seconds, Fetched: 5 row(s)
hive>
```

#### Another example
##### this will duplicate any String passed to the function

```
compile `import org.apache.hadoop.hive.ql.exec.UDF \;
import org.apache.hadoop.io.Text \;
public class Duplicate extends UDF {
  public Text evaluate(Text s){
    if (s == null) return null \; 
	return new Text(s.toString() * 2) \;
  }
} ` AS GROOVY NAMED Duplicate.groovy;

CREATE TEMPORARY FUNCTION Duplicate as 'Duplicate';
SELECT Duplicate(description) FROM sample_08 limit 5;

All OccupationsAll Occupations
Management occupationsManagement occupations
Chief executivesChief executives
General and operations managersGeneral and operations managers
LegislatorsLegislators
```

#### JSON Parsing UDF
```
compile `import org.apache.hadoop.hive.ql.exec.UDF \;
import groovy.json.JsonSlurper \;
import org.apache.hadoop.io.Text \;
public class JsonExtract extends UDF {
  public int evaluate(Text a){
    def jsonSlurper = new JsonSlurper() \;
    def obj = jsonSlurper.parseText(a.toString())\;
    return  obj.val1\;
  }
} ` AS GROOVY NAMED json_extract.groovy;

CREATE TEMPORARY FUNCTION json_extract as 'JsonExtract';
SELECT json_extract('{"val1": 2}') from date_dim limit 1;

2
```

#### Math Operations
```
compile `import org.apache.hadoop.hive.ql.exec.UDF \;
public class Pyth extends UDF {
  public double evaluate(double a, double b){
	return Math.sqrt((a*a) + (b*b)) \;
  }
} ` AS GROOVY NAMED Pyth.groovy;

CREATE TEMPORARY FUNCTION Pyth as 'Pyth';
SELECT Pyth(3,4) FROM src limit 1;

5.0
```
