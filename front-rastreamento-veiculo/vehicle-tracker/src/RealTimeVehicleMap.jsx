import React, { useState, useEffect, useRef } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import axios from "axios";

// Componente principal para o mapa em tempo real
const RealTimeVehicleMap = () => {
  // Estados para armazenar informações sobre os veículos, veículo selecionado e o status da conexão WebSocket
  const [vehicles, setVehicles] = useState([]); // Lista de veículos com suas posições
  const [selectedVehicle, setSelectedVehicle] = useState(null); // Veículo atualmente selecionado no mapa
  const [isConnected, setIsConnected] = useState(false); // Status da conexão WebSocket

  // Referência para o WebSocket
  const wsRef = useRef(null);

  // URLs para chamadas de API e WebSocket
  const API_BASE_URL = "http://localhost:8080/api/vehicles";
  const WS_URL = "ws://localhost:8080/ws";

  // Função para criar um ícone personalizado para os veículos (um carro estilizado)
  const createCarIcon = (size = 35) =>
    new L.DivIcon({
      html: `<div style="font-size: ${size}px; color: black;">🚗</div>`, // Ícone com um emoji de carro
      className: "car-icon",
      iconSize: [size, size],
      iconAnchor: [size / 2, size], // Âncora ajustada para centralizar o ícone
    });

  // Função para buscar a lista inicial de veículos da API
  const fetchVehicles = async () => {
    try {
      const response = await axios.get(API_BASE_URL); // Requisição GET para buscar veículos
      setVehicles(response.data); // Atualiza a lista de veículos
    } catch (error) {
      console.error("Erro ao carregar veículos:", error);
    }
  };

  // Função para conectar ao WebSocket e escutar atualizações em tempo real
  const connectWebSocket = () => {
    try {
      const socket = new WebSocket(WS_URL);

      // Evento disparado ao abrir a conexão
      socket.onopen = () => {
        console.log("Conexão WebSocket estabelecida.");
        setIsConnected(true); // Atualiza o status da conexão
      };

      // Evento disparado ao receber mensagens do WebSocket
      socket.onmessage = (event) => {
        try {
          const updatedVehicles = JSON.parse(event.data); // Parse da mensagem recebida
          updateVehicles(updatedVehicles); // Atualiza a lista de veículos
        } catch (error) {
          console.error("Erro ao processar mensagem WebSocket:", error);
        }
      };

      // Evento disparado ao fechar a conexão
      socket.onclose = () => {
        console.warn("Conexão WebSocket encerrada.");
        setIsConnected(false); // Atualiza o status da conexão
        reconnectWebSocket(); // Tenta reconectar
      };

      // Evento disparado em caso de erro no WebSocket
      socket.onerror = (error) => {
        console.error("Erro no WebSocket:", error);
        setIsConnected(false);
        reconnectWebSocket();
      };

      // Armazena a referência ao WebSocket
      wsRef.current = socket;
    } catch (error) {
      console.error("Erro ao conectar WebSocket:", error);
      setIsConnected(false);
      reconnectWebSocket();
    }
  };

  // Função para atualizar a lista de veículos com base em atualizações recebidas
  const updateVehicles = (updatedVehicles) => {
    setVehicles((prevVehicles) => mergeVehicleUpdates(prevVehicles, updatedVehicles));
  };

  // Função auxiliar para mesclar atualizações na lista existente de veículos
  const mergeVehicleUpdates = (prevVehicles, updatedVehicles) => {
    const updatedList = [...prevVehicles];
    updatedVehicles.forEach((updatedVehicle) => {
      const index = updatedList.findIndex((v) => v.id === updatedVehicle.id); // Encontra veículo pelo ID
      if (index !== -1) {
        updatedList[index] = updatedVehicle; // Atualiza veículo existente
      } else {
        updatedList.push(updatedVehicle); // Adiciona novo veículo
      }
    });
    return updatedList;
  };

  // Função para tentar reconectar ao WebSocket após desconexão
  const reconnectWebSocket = () => {
    console.log("Tentando reconectar ao WebSocket...");
    setTimeout(() => {
      connectWebSocket();
    }, 5000); // Aguarda 5 segundos antes de tentar reconectar
  };

  // Função para buscar o endereço com base em coordenadas geográficas
  const fetchAddress = async (lat, lon) => {
    try {
      const response = await axios.get(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}`
      );
      return response.data.address || {}; // Retorna o endereço ou um objeto vazio
    } catch (error) {
      console.error("Erro ao buscar endereço:", error);
      return {};
    }
  };

  // Função para alternar entre temas de mapa claro e escuro com base na hora atual
  const getTileLayerUrl = () => {
    const currentHour = new Date().getHours();
    return currentHour >= 18 || currentHour < 6
      ? "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
      : "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
  };

  // Efeito colateral para buscar veículos e conectar ao WebSocket na montagem do componente
  useEffect(() => {
    fetchVehicles(); // Busca a lista inicial de veículos
    connectWebSocket(); // Conecta ao WebSocket
    return () => {
      if (wsRef.current) {
        wsRef.current.close(); // Fecha o WebSocket ao desmontar
      }
    };
  }, []);

  // Componente para centralizar o mapa em um veículo selecionado
  const ZoomToVehicle = ({ vehicle }) => {
    const map = useMap(); // Obtém a instância do mapa
    useEffect(() => {
      if (vehicle) {
        map.setView([vehicle.latitude, vehicle.longitude], 18, { animate: true });
      }
    }, [vehicle, map]);
    return null;
  };

  // Componente para buscar e exibir o endereço de um ponto no mapa
  const FetchAddressPopup = ({ lat, lon }) => {
    const [address, setAddress] = useState(null);

    useEffect(() => {
      const fetchData = async () => {
        const addr = await fetchAddress(lat, lon); // Busca o endereço
        setAddress(addr);
      };
      fetchData();
    }, [lat, lon]);

    if (!address) {
      return <span>Carregando endereço...</span>; // Exibe uma mensagem de carregamento
    }

    // Renderiza o endereço completo
    return (
      <>
        Rua: {address.road || "Desconhecido"}
        <br />
        Bairro: {address.suburb || "Desconhecido"}
        <br />
        Cidade: {address.city || address.town || address.village || "Desconhecido"}
      </>
    );
  };

  // Renderização do mapa e status do WebSocket
  return (
    <div style={styles.container}>
      <MapContainer
        center={[0, 0]} // Posição inicial do mapa
        zoom={2} // Zoom inicial
        style={styles.map} // Estilo do mapa
      >
        <TileLayer
          url={getTileLayerUrl()} // URL da camada de tiles do mapa
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        />
        {vehicles.map((vehicle) => (
          <Marker
            key={vehicle.id} // Identificador único do veículo
            position={[vehicle.latitude, vehicle.longitude]} // Posição do marcador
            icon={createCarIcon()} // Ícone do marcador
            eventHandlers={{
              click: () => setSelectedVehicle(vehicle), // Define veículo selecionado ao clicar
            }}
          >
            <Popup>
              <strong>{vehicle.name}</strong> {/* Nome do veículo */}
              <br />
              <FetchAddressPopup lat={vehicle.latitude} lon={vehicle.longitude} /> {/* Endereço */}
              <br />
              Hora: {new Date(vehicle.timestamp).toLocaleTimeString()} {/* Hora do último registro */}
            </Popup>
          </Marker>
        ))}
        {selectedVehicle && <ZoomToVehicle vehicle={selectedVehicle} />} {/* Centraliza no veículo selecionado */}
      </MapContainer>
      <div style={{ ...styles.status, color: isConnected ? "green" : "red" }}>
        Status do WebSocket: {isConnected ? "Conectado" : "Desconectado"} {/* Exibe status da conexão */}
      </div>
    </div>
  );
};

// Estilos utilizados no componente
const styles = {
  container: {
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    height: "100vh",
    backgroundColor: "#f0f0f0",
  },
  map: {
    width: "80vw",
    height: "80vh",
    border: "2px solid #ccc",
    borderRadius: "10px",
    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)",
  },
  status: {
    marginTop: "10px",
    fontSize: "14px",
  },
};

export default RealTimeVehicleMap;
