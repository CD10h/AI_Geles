import axios from "axios";

export async function getPhoto(filename:any) {
  return axios.get(`/files/${filename}`);
}
