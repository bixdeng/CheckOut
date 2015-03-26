<?php
require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

if(isset($_POST['weight'])) {
	$weight = $POST['weight'];

	 // mysql inserting a new row
	$query = mysql_query("INSERT INTO products(weight) VALUES ('$weight')");
 	
 	$result = $query;
 	if ($result) 
 		echo "hell yeah";
 	else
 		echo "well fuck";
} else {
	echo "shit...";
}

// echo $data; 

?>

