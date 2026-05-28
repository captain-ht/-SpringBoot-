import axios from "axios";
import { ElMessage } from "element-plus";
import { getToken } from "@/utils/storage";

export async function downloadFile(url, filename, method = "get", data) {
  try {
    const response = await axios({
      url,
      method,
      data,
      responseType: "blob",
      headers: {
        Authorization: getToken() ? `Bearer ${getToken()}` : undefined
      }
    });
    const blob = new Blob([response.data]);
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    link.click();
    URL.revokeObjectURL(link.href);
  } catch (error) {
    ElMessage.error("文件下载失败");
  }
}
