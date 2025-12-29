import { getSkin } from '@/utils/auth'
export const setSkin = function () {
    setTimeout(() => {
        let skin = getSkin()
        if (skin == null) {
            skin = "shallow"
        }
        // 检查是否已经存在主题链接，避免重复添加
        let existingLink = document.getElementById("theme");
        if (existingLink) {
            existingLink.href = `/assets/${skin}.css`;
        } else {
            let link = document.createElement("link");
            link.type = "text/css";
            link.id = "theme";
            link.rel = "stylesheet";
            link.href = `/assets/${skin}.css`;
            document.getElementsByTagName("head")[0].appendChild(link);
        }
    }, 100);
}