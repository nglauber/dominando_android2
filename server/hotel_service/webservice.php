<?php
$servername = "localhost";
$username = "root";
$password = "";
$conn = new mysqli($servername, $username, $password);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
$sql = "CREATE DATABASE IF NOT EXISTS hotel_db";
if (!$conn->query($sql) === TRUE) {
    echo "Erro ao criar banco de dados: " . $conn->error;
}
$sql = "CREATE TABLE IF NOT EXISTS hotel_db.Hotel (
id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
nome VARCHAR(100) NOT NULL,
endereco VARCHAR(100) NOT NULL,
estrelas DECIMAL(2,1)
)";
if ($conn->query($sql) === FALSE) {
    echo "Erro ao criar tabela: " . $conn->error;
}
$metodoHttp = $_SERVER['REQUEST_METHOD'];
if ($metodoHttp == 'POST') {
	$stmt = $conn->prepare(
		"INSERT INTO hotel_db.Hotel (nome, endereco, estrelas) VALUES (?, ?, ?)");
    $json = json_decode(file_get_contents('php://input'));
    $nome     = $json->{'nome'};
    $endereco = $json->{'endereco'};
    $estrelas = $json->{'estrelas'};
    $stmt->bind_param("ssd", $nome, $endereco, $estrelas);
    $stmt->execute();
    $stmt->close();
    $id = $conn->insert_id;
    $jsonRetorno = array("id"=>$id);
    echo json_encode($jsonRetorno);
} else if ($metodoHttp == 'GET') {
    $jsonArray = array();
    $sql = "SELECT id, nome, endereco, estrelas FROM hotel_db.Hotel";
    $result = $conn->query($sql);
    if ($result && $result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $jsonLinha = array(
                 "id"       => $row["id"],
                "nome"     => $row["nome"],
                "endereco" => $row["endereco"],
                "estrelas" => (float)$row["estrelas"]);
            $jsonArray[] = $jsonLinha;    	    
        }
    }
    echo json_encode($jsonArray);
} else if ($metodoHttp == 'PUT') {
    $stmt = $conn->prepare(
        "UPDATE hotel_db.Hotel SET nome=?, endereco=?, estrelas=?  WHERE id=?");
    $json  = json_decode(file_get_contents('php://input'));
    $id       = $json->{'id'};
    $nome     = $json->{'nome'};
    $endereco = $json->{'endereco'};
    $estrelas = $json->{'estrelas'};
    $stmt->bind_param("ssdi", $nome, $endereco, $estrelas, $id);
    $stmt->execute();
    $stmt->close();
    $jsonRetorno = array("id"=>$id);
    echo json_encode($jsonRetorno);
} else if ($metodoHttp == 'DELETE') {
    $stmt = $conn->prepare("DELETE FROM hotel_db.Hotel WHERE id=?");
    $segments = explode("/", $_SERVER["REQUEST_URI"]);
    $id = $segments[count($segments)-1];
    $stmt->bind_param("i", $id);
    $stmt->execute();
    $stmt->close();
    $jsonRetorno = array("id"=>$id);
    echo json_encode($jsonRetorno);
}
$conn->close();
?>
