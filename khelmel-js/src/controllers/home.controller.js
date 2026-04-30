class HomeController {
    static index(req, res) {
        res.render('pages/home', {
            title: "Khelmel — Thoda Khel, Zyada Mel"
        });
    }
}

module.exports = HomeController;
