import axios from "axios";

const pokemonAPi = axios.create({
  baseURL: "http://localhost:8080",
});

export default pokemonAPi;
