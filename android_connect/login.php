<?php
 
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once 'db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_POST["email"])&&isset($_POST["password"])) {
    $email = $_POST['email'];
    $password=$_POST['password'];
 
    // get a product from products table
    $result = mysql_query("SELECT * FROM EventOwner WHERE email ='$email' and password='$password'");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $event_owner = array();
            $event_owner["user_id"] = $result["user_id"];
            $event_owner["email"] = $result["name"];
            $event_owner["price"] = $result["price"];
            $event_owner["description"] = $result["description"];
            $event_owner["created_at"] = $result["created_at"];
            $event_owner["updated_at"] = $result["updated_at"];
            // success
            $response["success"] = 1;
 
            // user node
            $response["event_owner"] = array();
 
            array_push($response["event_owner"], $product);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No event_owner found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No event_owner found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>