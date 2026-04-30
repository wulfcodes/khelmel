class BingoController {
    static index(req, res) {
        res.render('pages/bingo/index', {
            title: "Bingo — Khelmel"
        });
    }
}

module.exports = BingoController;
