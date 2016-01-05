# Groovy UDF example
#### Can be compiled at the run time
#### Currently can only work in "hive" shell, not working in beeline

```
su guest
hive
```

### paste the following code into the hive shell
# this will use Groovy String replace function to replace all instances of lower case 'e' with 'E'

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

#### now create a temporary function to leverage the Groovy UDF

```
CREATE TEMPORARY FUNCTION Replace as 'Replace';
```

#### now you can use the function in your SQL

```
SELECT Replace(description) FROM sample_08 limit 5;
```

### Another example
# this will duplicate any String passed to the function

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
```
