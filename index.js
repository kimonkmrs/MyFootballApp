const express = require('express');
const mysql = require('mysql2');
const bodyParser = require('body-parser');
const cors = require('cors');
const http = require('http');
const socketIo = require('socket.io');
const moment = require('moment-timezone'); // Import moment-timezone for timezone conversion
const Joi = require('joi'); // Import Joi for validation

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
    cors: {
        origin: "*",
    }
});

// Middleware setup
app.use(express.json()); // Correctly set up to parse JSON bodies
app.use(express.urlencoded({ extended: true }));
//app.use(cors());
//app.use(bodyParser.json()); // Ensure JSON parsing middleware is used

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
// MySQL connection setup
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'Kimonas9912!',
    database: 'FootballApp'
});

db.connect((err) => {
    if (err) {
        console.error('Database connection failed:', err.stack);
        return;
    }
    console.log('Connected to MySQL database.');
});

// Define a route for the root path NEW
app.get('/', (req, res) => {
    res.send('Welcome to the Football App Backend!');
});

// Joi schema for validation
const matchSchema = Joi.object({
    team1ID: Joi.number().integer().required(),
    team2ID: Joi.number().integer().required(),
    scoreTeam1: Joi.number().integer().required(),
    scoreTeam2: Joi.number().integer().required(),
    matchDate: Joi.string().pattern(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/).required()
});
// WebSocket connection handler
io.on('connection', (socket) => {
    console.log('New client connected');
    
    // Handle disconnection
    socket.on('disconnect', () => {
        console.log('Client disconnected');
    });
});
// Route to fetch all teams
app.get('/teams', (req, res) => {
    db.query('SELECT * FROM Teams', (err, results) => {
        if (err) {
            console.error('Error fetching teams:', err);
            return res.status(500).json({ error: 'Failed to fetch teams' });
        }
        res.json(results);
    });
});

// Route to fetch all players
// Route to fetch players for a specific team ID
app.get('/players', (req, res) => {
    db.query('SELECT * FROM Players', (err, results) => {
        if (err) {
            console.error('Error fetching players:', err);
            return res.status(500).json({ error: 'Failed to fetch players' });
        }
        res.json(results);
    });
});
//fetch players from matches
// Route to fetch players for all teams involved in matches
// Route to fetch players for a specific team ID
app.get('/playersByTeam', (req, res) => {
    const query = `
        SELECT * FROM Players WHERE TeamID IN (
            SELECT Team1ID FROM Matches WHERE DATE(MatchDate) = CURDATE()
            UNION
            SELECT Team2ID FROM Matches WHERE DATE(MatchDate) = CURDATE()
        )
    `;

    db.query(query, (err, results) => {
        if (err) {
            console.error('Error fetching players:', err);
            return res.status(500).json({ error: 'Failed to fetch players' });
        }
        res.json(results);
    });
});
app.patch('/players/:playerId/updateMatchId', (req, res) => {
    const playerId = req.params.playerId;
    let matchId = req.query.matchId;

    // Convert "null" string to actual null
    if (matchId === 'null') {
        matchId = null;
    }

    // Update the player's MatchID
    const sql = `UPDATE Players SET MatchID = ? WHERE PlayerID = ?`;

    db.query(sql, [matchId, playerId], (err, result) => {
        if (err) {
            console.error('Error updating MatchID:', err);
            return res.status(500).json({ error: 'Failed to update MatchID' });
        }
        // Emit WebSocket event to notify clients that the MatchID has been updated
        io.emit('matchIdUpdated', { playerId, matchId });
        res.json({ message: 'MatchID updated successfully' });
    });
});
app.post('/players/assignMatch', (req, res) => {
    const playerId = req.body.playerId; // Retrieve playerId from request body
    const matchId = req.body.matchId;   // Retrieve matchId from request body

    console.log(`Request body: ${JSON.stringify(req.body)}`); // Log the request body
    // Check if player is already assigned to the match
    const checkSql = `SELECT * FROM PlayerMatches WHERE PlayerID = ? AND MatchID = ?`;
    db.query(checkSql, [playerId, matchId], (err, results) => {
        if (err) {
            console.error('Error checking record existence:', err);
            return res.status(500).json({ error: 'Failed to check record existence' });
        }

        if (results.length > 0) {
            console.log('Player is already assigned to this match'); // Log if the player is already assigned
            return res.status(400).json({ error: 'Player is already assigned to this match' });
        }

        // Insert the new player-match association if it doesn't exist
        const sql = `INSERT INTO PlayerMatches (PlayerID, MatchID) VALUES (?, ?)`;
        db.query(sql, [playerId, matchId], (err, result) => {
            if (err) {
                console.error('Error assigning match to player:', err);
                return res.status(500).json({ error: 'Failed to assign match to player' });
            }
            console.log(`Player ${playerId} successfully assigned to match ${matchId}`); // Log success
            res.json({ message: 'Player successfully assigned to match' });
        });
    });
});



// Route to get matches assigned to a player
app.get('/players/matches', (req, res) => {
    const playerId = req.params.playerId;

    const sql = 'SELECT * FROM PlayerMatches WHERE PlayerID = ?';

    db.query(sql, [playerId], (err, results) => {
        if (err) {
            console.error('Error fetching player matches:', err);
            return res.status(500).json({ error: 'Failed to fetch player matches' });
        }
        res.json(results);
    });
});

// Route to add a player
app.post('/players/add', (req, res) => {
    const { playerName, teamID, playerPosition } = req.body;  // Accept playerPosition from the request body
    console.log('Received data:', { playerName, teamID, playerPosition }); // Log the incoming request data

    // Validate input
    if (!playerName || !teamID || !playerPosition) {  // Check for playerPosition too
        return res.status(400).json({ error: 'Player name, team ID, and position are required' });
    }

    // Check if teamId exists in the Teams table
    const teamCheckSql = 'SELECT * FROM Teams WHERE TeamID = ?';
    db.query(teamCheckSql, [teamID], (err, results) => {
        if (err) {
            console.error('Error checking team ID:', err);
            return res.status(500).json({ error: 'Failed to check team ID' });
        }
        if (results.length === 0) {
            return res.status(400).json({ error: 'Invalid team ID' });
        }

        // Insert player into the database, now including playerPosition
        const sql = 'INSERT INTO Players (PlayerName, TeamID, Position) VALUES (?, ?, ?)';
        db.query(sql, [playerName, teamID, playerPosition], (err, results) => {
            if (err) {
                console.error('Error inserting player:', err);
                return res.status(500).json({ error: 'Failed to add player' });
            }
            res.status(201).json({ message: 'Player added successfully', playerId: results.insertId });
        });
    });
});



app.delete('/players/:playerId/removeMatch', (req, res) => {
    const playerId = req.params.playerId;
    const matchId = req.query.matchId; // Use req.query to capture the query parameter

    if (!matchId) {
        return res.status(400).json({ error: 'Match ID is required' });
    }

    // First, set the player's stats (goals, yellow cards, red cards) to 0 for this match
    const resetStatsSql = `UPDATE PlayerMatches SET goals = 0, yellowCards = 0, redCards = 0 WHERE PlayerID = ? AND MatchID = ?`;

    db.query(resetStatsSql, [playerId, matchId], (err, result) => {
        if (err) {
            console.error('Error resetting player stats:', err);
            return res.status(500).json({ error: 'Failed to reset player stats' });
        }

        // After resetting the stats, delete the player from the match
        const deleteSql = `DELETE FROM PlayerMatches WHERE PlayerID = ? AND MatchID = ?`;

        db.query(deleteSql, [playerId, matchId], (err, result) => {
            if (err) {
                console.error('Error removing match from player:', err);
                return res.status(500).json({ error: 'Failed to remove player from match' });
            }

            if (result.affectedRows === 0) {
                return res.status(404).json({ error: 'Player or Match not found' });
            }

            res.json({ message: 'Player successfully removed from match' });
        });
    });
});







// Route to fetch all matches
app.get('/matches', (req, res) => {
    db.query('SELECT * FROM Matches', (err, results) => {
        if (err) {
            console.error('Error fetching matches:', err);
            return res.status(500).json({ error: 'Failed to fetch matches' });
        }
        res.json(results);
    });
});
// Route to fetch matches for today
app.get('/matches/today', (req, res) => {
    console.log('Request received for /matches/today');
    const startOfToday = moment().tz('Europe/Athens').startOf('day').format('YYYY-MM-DD HH:mm:ss');
    const endOfToday = moment().tz('Europe/Athens').endOf('day').format('YYYY-MM-DD HH:mm:ss');

    const query = `SELECT * FROM Matches WHERE MatchDate BETWEEN ? AND ?`;

    db.query(query, [startOfToday, endOfToday], (err, results) => {
        if (err) {
            console.error('Error fetching today\'s matches:', err);
            return res.status(500).json({ error: 'Failed to fetch today\'s matches' });
        }

        const matchesInGreeceTime = results.map(match => ({
            ...match,
            MatchDate: moment(match.MatchDate).tz('Europe/Athens').format('DD-MM-YYYY HH:mm:ss')
        }));

        res.json(matchesInGreeceTime.length ? matchesInGreeceTime : { message: 'No matches today' });
    });
});

// Route to update player statistics
app.post('/match/:matchId/player/:playerId/updateStat', (req, res) => {
    const { statType, value } = req.body;
    const matchId = req.params.matchId;  // Accept matchId from path parameters
    const playerId = req.params.playerId; // Accept playerId from path parameters

    // Validate input
    if (!playerId || !statType || !matchId || value === undefined) {
        return res.status(400).json({ error: 'playerId, statType, matchId and value are required' });
    }

    // Ensure statType is one of the allowed values
    if (!['goals', 'yellowCards', 'redCards'].includes(statType)) {
        return res.status(400).json({ error: 'Invalid statType. Allowed values are "goals", "yellowCards", "redCards"' });
    }

    // Construct the SQL query to update the player statistic
    let column;
    switch (statType) {
        case 'goals':
            column = 'Goals';
            break;
        case 'yellowCards':
            column = 'YellowCards';
            break;
        case 'redCards':
            column = 'RedCards';
            break;
    }

    const sql = `UPDATE PlayerMatches SET ${column} = ? WHERE MatchID = ? AND PlayerID = ?`;

    db.query(sql, [value, matchId, playerId], (err, result) => {
        if (err) {
            console.error('Error updating player statistics:', err);
            return res.status(500).json({ error: 'Failed to update player statistics' });
        }

        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Player not found for the given match' });
        }

        res.json({ message: 'Player statistics updated successfully' });
    });
});



// Route to insert a new match
app.post('/matches/insert', (req, res) => {
    // Validate the request data using Joi
    const { error } = matchSchema.validate(req.body);

    if (error) {
        console.error('Validation error:', error.details[0].message);
        return res.status(400).json({ error: error.details[0].message });
    }

    const { team1ID, team2ID, scoreTeam1, scoreTeam2, matchDate } = req.body;

    // Check if matchDate is in the correct format
    const formattedMatchDate = moment(matchDate, 'YYYY-MM-DD HH:mm:ss', true).format('YYYY-MM-DD HH:mm:ss');
    if (!moment(formattedMatchDate, 'YYYY-MM-DD HH:mm:ss', true).isValid()) {
        console.error('Invalid date format:', matchDate);
        return res.status(400).json({ error: 'Invalid date format. Please use YYYY-MM-DD HH:mm:ss' });
    }

    // Query to insert a new match
    const query = `
        INSERT INTO Matches (Team1ID, Team2ID, Team1Name, Team2Name, ScoreTeam1, ScoreTeam2, MatchDate)
        VALUES (?, ?, 
            (SELECT TeamName FROM Teams WHERE TeamID = ?), 
            (SELECT TeamName FROM Teams WHERE TeamID = ?),
            ?, ?, ?)
    `;

    db.query(query, [team1ID, team2ID, team1ID, team2ID, scoreTeam1, scoreTeam2, formattedMatchDate], (err, results) => {
        if (err) {
            console.error('Error inserting match:', err);
            return res.status(500).json({ error: 'Failed to insert match' });
        }
        console.log('Match inserted successfully:', results);
        res.json({ status: 'success', message: 'Match inserted successfully' });
    });
});
// Route to delete a match
app.delete('/matches/delete/:id', (req, res) => {
    const matchId = req.params.id;

    // Validate the match ID
    if (!Number.isInteger(parseInt(matchId))) {
        return res.status(400).json({ error: 'Invalid match ID' });
    }

    // Query to delete the match
    const query = 'DELETE FROM Matches WHERE MatchID = ?';

    db.query(query, [matchId], (err, results) => {
        if (err) {
            console.error('Error deleting match:', err);
            return res.status(500).json({ error: 'Failed to delete match' });
        }
        if (results.affectedRows === 0) {
            return res.status(404).json({ error: 'Match not found' });
        }
        console.log('Match deleted successfully:', results);
        res.json({ status: 'success', message: 'Match deleted successfully' });
    });
});

// Listen for connections from clients
io.on('connection', (socket) => {
    console.log('A user connected');

    socket.on('disconnect', () => {
        console.log('User disconnected');
    });
});

// Start the server
// Route to insert a new match
app.post('/matches/insert', (req, res) => {
    const { error } = matchSchema.validate(req.body);

    if (error) {
        console.error('Validation error:', error.details[0].message);
        return res.status(400).json({ error: error.details[0].message });
    }

    const { team1ID, team2ID, scoreTeam1, scoreTeam2, matchDate } = req.body;

    const formattedMatchDate = moment(matchDate, 'YYYY-MM-DD HH:mm:ss', true).format('YYYY-MM-DD HH:mm:ss');
    if (!moment(formattedMatchDate, 'YYYY-MM-DD HH:mm:ss', true).isValid()) {
        console.error('Invalid date format:', matchDate);
        return res.status(400).json({ error: 'Invalid date format. Please use YYYY-MM-DD HH:mm:ss' });
    }

    const query = 'INSERT INTO Matches (Team1ID, Team2ID, ScoreTeam1, ScoreTeam2, MatchDate) VALUES (?, ?, ?, ?, ?)';
    const values = [team1ID, team2ID, scoreTeam1, scoreTeam2, formattedMatchDate];

    db.query(query, values, (err, result) => {
        if (err) {
            console.error('Error inserting match:', err);
            return res.status(500).json({ error: 'Failed to insert match' });
        }
        res.status(201).json({ message: 'Match inserted successfully' });
    });
});
// Route to update player's position for a specific match
app.post('/match/:matchId/player/:playerId/updatePosition', (req, res) => {
    const { playerId, matchId } = req.params;
    const { position } = req.body;

    // Validate input
    if (!position || !playerId || !matchId) {
        return res.status(400).json({ error: 'Position, playerId, and matchId are required' });
    }

    // Construct the SQL query to update the player's position
    const sql = `UPDATE PlayerMatches SET Position = ? WHERE PlayerID = ? AND MatchID = ?`;

    // Execute the query with the provided data
    db.query(sql, [position, playerId, matchId], (err, result) => {
        if (err) {
            console.error('Error updating player position:', err);
            return res.status(500).json({ error: 'Failed to update player position' });
        }

        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Player not found or match not found' });
        }

        // Successfully updated the player's position
        res.json({ message: 'Player position updated successfully' });
    });
});
//team details
app.get('/team-details', async (req, res) => {
    const { teamName } = req.query;
    const query = `
          SELECT 
        t.TeamName AS TeamName, 
        g.GroupName AS GroupName, 
        t.Wins, 
        t.Draws, 
        t.Losses, 
        t.MP, 
        t.Points, 
        t.Position, 
        t.GF, 
        t.GA, 
        (t.GF - t.GA) AS GD, 
        COUNT(p.PlayerID) AS SquadSize 
    FROM Teams t 
    LEFT JOIN Players p ON t.TeamID = p.TeamID 
    JOIN TeamGroups g ON t.GroupID = g.GroupId 
    WHERE t.TeamName = ?
    GROUP BY t.TeamName, g.GroupName, t.Wins, t.Draws, t.Losses, t.MP, t.Points, t.Position, t.GF, t.GA;
    `;
    db.query(query, [teamName], (err, results) => {
        if (err) {
            console.error('Error fetching team details:', err);
            return res.status(500).json({ error: 'Failed to fetch team details' });
        }
        if (results.length === 0) {
            return res.status(404).json({ error: 'Team not found' });
        }
        res.json(results[0]);
    });
});



// Route to fetch standings
app.get('/standings/general', (req, res) => {
    const query = `
         WITH RankedTeams AS (
            SELECT 
                A.GroupID,
                A.GroupName, 
                A.TeamName, 
                COUNT(A.MatchID) AS MP,       -- Matches Played
                SUM(A.Wins) AS Wins,          -- Wins
                SUM(A.Losses) AS Losses,      -- Losses
                SUM(A.Draws) AS Draws,        -- Draws
                SUM(A.GoalsFor) AS GF,        -- Goals For
                SUM(A.GoalsAgainst) AS GA,    -- Goals Against
                (SUM(A.GoalsFor) - SUM(A.GoalsAgainst)) AS GD,  -- Goal Difference
                (SUM(A.Wins) * 3 + SUM(A.Draws) * 1 ) AS Points  -- Points
            FROM (
                -- Team1 (Home team perspective)
                SELECT 
                    t1.TeamName, 
                    g.GroupName,
                    g.GroupID,
                    m.MatchID,
                    m.MatchDate,
                    CASE 
                        WHEN m.ScoreTeam1 > m.ScoreTeam2 THEN 1 ELSE 0 
                    END AS Wins,
                    CASE 
                        WHEN m.ScoreTeam1 < m.ScoreTeam2 THEN 1 ELSE 0 
                    END AS Losses,
                    CASE 
                        WHEN m.ScoreTeam1 = m.ScoreTeam2 THEN 1 ELSE 0 
                    END AS Draws,
                    m.ScoreTeam1 AS GoalsFor,     -- Goals For for Team1
                    m.ScoreTeam2 AS GoalsAgainst  -- Goals Against for Team1
                FROM Teams t1
                JOIN Matches m ON t1.TeamID = m.Team1ID
                JOIN TeamGroups g ON t1.GroupID = g.GroupID
                 WHERE date(m.MatchDate) <= CURRENT_DATE()  -- Only include matches up to the current date
                 and (m.ScoreTeam1<>0 or m.ScoreTeam2<>0)

                UNION ALL

                -- Team2 (Away team perspective)
                SELECT 
                    t2.TeamName, 
                    g.GroupName, 
                    g.GroupID,
                    m.MatchID, 
                    m.MatchDate,
                    CASE 
                        WHEN m.ScoreTeam2 > m.ScoreTeam1 THEN 1 ELSE 0 
                    END AS Wins,
                    CASE 
                        WHEN m.ScoreTeam2 < m.ScoreTeam1 THEN 1 ELSE 0 
                    END AS Losses,
                    CASE 
                        WHEN m.ScoreTeam2 = m.ScoreTeam1 THEN 1 ELSE 0 
                    END AS Draws,
                    m.ScoreTeam2 AS GoalsFor,     -- Goals For for Team2
                    m.ScoreTeam1 AS GoalsAgainst  -- Goals Against for Team2
                FROM Teams t2
                JOIN Matches m ON t2.TeamID = m.Team2ID
                JOIN TeamGroups g ON t2.GroupID = g.GroupID
                 WHERE date(m.MatchDate) <= CURRENT_DATE()  -- Only include matches up to the current date
                  AND (m.ScoreTeam1 <> 0 OR m.ScoreTeam2 <> 0) 
            ) A
            -- Filter by GroupName
            GROUP BY A.TeamName, A.GroupName, A.GroupID
        )

        -- Rank the teams within the group based on points and goal difference
        SELECT 
            ROW_NUMBER() OVER (ORDER BY Points DESC, GD DESC,GF desc,Teamname) AS Position, -- Group Ranking
            GroupID,
            GroupName, 
            TeamName, 
            MP, 
            Wins, 
            Losses, 
            Draws, 
            GF, 
            GA, 
            GD,         -- Goal Difference
            Points
        FROM RankedTeams
        ORDER BY Position, GD DESC,GF DESC,TeamName;
    `;

    db.query(query, (err, results) => {
        if (err) {
            console.error('Error fetching standings:', err);
            return res.status(500).json({ error: 'Failed to fetch standings' });
        }
        res.json(results);
    });
});
app.get('/standings/group/:groupName', (req, res) => {
    const groupName = req.params.groupName;

    const query = `
        WITH RankedTeams AS (
            SELECT 
                A.GroupID,
                A.GroupName, 
                A.TeamName, 
                COUNT(A.MatchID) AS MP,       -- Matches Played
                SUM(A.Wins) AS Wins,          -- Wins
                SUM(A.Losses) AS Losses,      -- Losses
                SUM(A.Draws) AS Draws,        -- Draws
                SUM(A.GoalsFor) AS GF,        -- Goals For
                SUM(A.GoalsAgainst) AS GA,    -- Goals Against
                (SUM(A.GoalsFor) - SUM(A.GoalsAgainst)) AS GD,  -- Goal Difference
                (SUM(A.Wins) * 3 + SUM(A.Draws) * 1 ) AS Points  -- Points
            FROM (
                -- Team1 (Home team perspective)
                SELECT 
                    t1.TeamName, 
                    g.GroupName,
                    g.GroupID,
                    m.MatchID,
                    m.MatchDate,
                    CASE 
                        WHEN m.ScoreTeam1 > m.ScoreTeam2 THEN 1 ELSE 0 
                    END AS Wins,
                    CASE 
                        WHEN m.ScoreTeam1 < m.ScoreTeam2 THEN 1 ELSE 0 
                    END AS Losses,
                    CASE 
                        WHEN m.ScoreTeam1 = m.ScoreTeam2 THEN 1 ELSE 0 
                    END AS Draws,
                    m.ScoreTeam1 AS GoalsFor,     -- Goals For for Team1
                    m.ScoreTeam2 AS GoalsAgainst  -- Goals Against for Team1
                FROM Teams t1
                JOIN Matches m ON t1.TeamID = m.Team1ID
                JOIN TeamGroups g ON t1.GroupID = g.GroupID
                 WHERE date(m.MatchDate) <= CURRENT_DATE()  -- Only include matches up to the current date
                 AND (m.ScoreTeam1 <> 0 OR m.ScoreTeam2 <> 0) 
                UNION ALL

                -- Team2 (Away team perspective)
                SELECT 
                    t2.TeamName, 
                    g.GroupName, 
                    g.GroupID,
                    m.MatchID, 
                    m.MatchDate,
                    CASE 
                        WHEN m.ScoreTeam2 > m.ScoreTeam1 THEN 1 ELSE 0 
                    END AS Wins,
                    CASE 
                        WHEN m.ScoreTeam2 < m.ScoreTeam1 THEN 1 ELSE 0 
                    END AS Losses,
                    CASE 
                        WHEN m.ScoreTeam2 = m.ScoreTeam1 THEN 1 ELSE 0 
                    END AS Draws,
                    m.ScoreTeam2 AS GoalsFor,     -- Goals For for Team2
                    m.ScoreTeam1 AS GoalsAgainst  -- Goals Against for Team2
                FROM Teams t2
                JOIN Matches m ON t2.TeamID = m.Team2ID
                JOIN TeamGroups g ON t2.GroupID = g.GroupID
                 WHERE date(m.MatchDate) <= CURRENT_DATE()  -- Only include matches up to the current date
                 AND (m.ScoreTeam1 <> 0 OR m.ScoreTeam2 <> 0) 
            ) A
            WHERE A.GroupName=?-- Filter by GroupName
            GROUP BY A.TeamName, A.GroupName, A.GroupID
        )

        -- Rank the teams within the group based on points and goal difference
        SELECT 
            ROW_NUMBER() OVER (ORDER BY Points DESC, GD DESC,GF desc,Teamname) AS Position, -- Group Ranking
            GroupID,
            GroupName, 
            TeamName, 
            MP, 
            Wins, 
            Losses, 
            Draws, 
            GF, 
            GA, 
            GD,         -- Goal Difference
            Points
        FROM RankedTeams
        ORDER BY Position, GD DESC,GF DESC,TeamName;
    `;

    db.query(query, [groupName], (err, results) => {
        if (err) {
            console.error('Error fetching group standings:', err);
            return res.status(500).json({ error: 'Failed to fetch group standings' });
        }
        res.json(results);
    });
});
// Route to fetch all team groups including a "General Table" option
app.get('/groups', (req, res) => {
    console.log('Received request for /groups'); // Debugging line

    const query = 'SELECT GroupID, GroupName FROM TeamGroups';
    db.query(query, (err, results) => {
        if (err) {
            console.error('Error fetching team groups:', err);
            return res.status(500).json({ error: 'Failed to fetch team groups' });
        }

        // Add "General Table" manually with GroupID = 0
        const generalTable = { GroupID: 0, GroupName: 'General Table' };
        results.unshift(generalTable); // Add "General Table" at the top

        res.json(results); // Send all group names with "General Table"
    });
});
// Route to fetch player statistics
app.get('/api/playerstats', (req, res) => {
    const query = `
        SELECT 
            P.PlayerName, 
            Pm.Position, 
            T.TeamName, 
            COUNT(DISTINCT pm.MatchID) AS MP,  -- Total Matches Played by the Player
            SUM(pm.Goals) AS GF,               -- Total Goals Scored by the Player
            SUM(CASE 
                WHEN T.TeamID = m.Team1ID THEN m.ScoreTeam2  -- For home team, goals conceded are from the away team
                ELSE m.ScoreTeam1  -- For away team, goals conceded are from the home team
            END) AS GA,                     -- Total Goals Conceded by the Player's Team
            SUM(CASE 
                WHEN (T.TeamID = m.Team1ID AND m.ScoreTeam1 > m.ScoreTeam2) OR 
                     (T.TeamID = m.Team2ID AND m.ScoreTeam2 > m.ScoreTeam1) THEN 1
                ELSE 0 
            END) AS Wins,                   -- Total Wins
            SUM(CASE 
                WHEN (T.TeamID = m.Team1ID AND m.ScoreTeam1 < m.ScoreTeam2) OR 
                     (T.TeamID = m.Team2ID AND m.ScoreTeam2 < m.ScoreTeam1) THEN 1
                ELSE 0 
            END) AS Losses,                 -- Total Losses
            SUM(CASE 
                WHEN m.ScoreTeam1 = m.ScoreTeam2 THEN 1
                ELSE 0 
            END) AS Draws                   -- Total Draws
        FROM 
            footballapp.Players P
        JOIN 
            footballapp.Teams T ON P.TeamID = T.TeamID
        JOIN 
            footballapp.PlayerMatches pm ON P.PlayerID = pm.PlayerID
        JOIN 
            footballapp.Matches m ON pm.MatchID = m.MatchID  -- Join to access match results
        WHERE 
            P.PlayerName IN (
                 /*SELECT a.PlayerName
                FROM (
                    SELECT PlayerName, COUNT(MatchID) AS MP 
                    FROM footballapp.PlayerMatches 
                    GROUP BY PlayerName 
                    HAVING MP > 0 -- Ensures only players with matches are included
                ) a
                
            )*/
             (SELECT PlayerName from footballapp.Players where PlayerID in(select a.PlayerID
                FROM (
                    SELECT PlayerID, COUNT(MatchID) AS MP 
                    FROM footballapp.PlayerMatches 
                    GROUP BY PlayerID 
                    HAVING MP > 0 -- Ensures only players with matches are included
                ) a)))
        GROUP BY 
            P.PlayerName, Pm.Position, T.TeamName
        ORDER BY GF DESC;
    `;

    db.query(query, (err, results) => {
        if (err) {
            console.error('Error fetching player stats:', err);
            return res.status(500).json({ error: 'Failed to fetch player statistics' });
        }
        res.json(results);
    });
});
// Route to fetch player statistics
app.get('/api/playerstats/byTeams/:teamName', (req, res) => {
    const teamName = req.params.teamName || '';  // Change here to get from params
    const query = `
        SELECT 
            P.PlayerName, 
            Pm.Position, 
            T.TeamName, 
            COUNT(DISTINCT pm.MatchID) AS MP, 
            SUM(pm.Goals) AS GF, 
            SUM(CASE 
                WHEN T.TeamID = m.Team1ID THEN m.ScoreTeam2 
                ELSE m.ScoreTeam1 
            END) AS GA, 
            SUM(CASE 
                WHEN (T.TeamID = m.Team1ID AND m.ScoreTeam1 > m.ScoreTeam2) OR 
                     (T.TeamID = m.Team2ID AND m.ScoreTeam2 > m.ScoreTeam1) THEN 1
                ELSE 0 
            END) AS Wins, 
            SUM(CASE 
                WHEN (T.TeamID = m.Team1ID AND m.ScoreTeam1 < m.ScoreTeam2) OR 
                     (T.TeamID = m.Team2ID AND m.ScoreTeam2 < m.ScoreTeam1) THEN 1
                ELSE 0 
            END) AS Losses, 
            SUM(CASE 
                WHEN m.ScoreTeam1 = m.ScoreTeam2 THEN 1
                ELSE 0 
            END) AS Draws 
        FROM 
            footballapp.Players P
        JOIN 
            footballapp.Teams T ON P.TeamID = T.TeamID
        JOIN 
            footballapp.PlayerMatches pm ON P.PlayerID = pm.PlayerID
        JOIN 
            footballapp.Matches m ON pm.MatchID = m.MatchID  
        WHERE 
            T.TeamName = ? 
        GROUP BY 
            P.PlayerName, Pm.Position, T.TeamName
        ORDER BY 
            GF DESC;
    `;

    // Execute the query with the team name as a parameter to prevent SQL injection
    db.query(query, [teamName], (err, results) => {
        if (err) {
            console.error('Error fetching player stats:', err);
            return res.status(500).json({ error: 'Failed to fetch player statistics' });
        }
        console.log(`Query executed: ${query}`);  // Log the executed query
        console.log(`Results:`, results);  // Log the results
        res.json(results);
    });
});




// Route to update match scores based on player goals
app.patch('/matches/:id/updateScoresFromPlayers', (req, res) => {
    const matchId = req.params.id;

    // Validate input
    if (!Number.isInteger(parseInt(matchId))) {
        return res.status(400).json({ error: 'Invalid match ID' });
    }

    // Start a transaction to ensure atomic updates
    db.beginTransaction((err) => {
        if (err) {
            console.error('Error starting transaction:', err);
            return res.status(500).json({ error: 'Failed to start transaction' });
        }

        // Update scores for Team1
        const updateTeam1ScoresQuery = `
            UPDATE Matches m
            JOIN (
                SELECT
                    m.MatchID,
                    COALESCE(SUM(pm.Goals), 0) AS Team1Goals
                FROM PlayerMatches pm
                JOIN Matches m ON m.MatchID = pm.MatchID
                JOIN Teams t1 ON t1.TeamID = m.Team1ID
                JOIN Players p ON p.TeamID = t1.TeamID AND p.PlayerID = pm.PlayerID
                WHERE m.MatchID = ?
                GROUP BY m.MatchID
            ) AS team1_scores ON m.MatchID = team1_scores.MatchID
            SET m.ScoreTeam1 = team1_scores.Team1Goals
        `;

        db.query(updateTeam1ScoresQuery, [matchId], (err, result) => {
            if (err) {
                return db.rollback(() => {
                    console.error('Error updating Team1 scores:', err);
                    res.status(500).json({ error: 'Failed to update Team1 scores' });
                });
            }

            // Update scores for Team2
            const updateTeam2ScoresQuery = `
                UPDATE Matches m
                JOIN (
                    SELECT
                        m.MatchID,
                        COALESCE(SUM(pm.Goals), 0) AS Team2Goals
                    FROM PlayerMatches pm
                    JOIN Matches m ON m.MatchID = pm.MatchID
                    JOIN Teams t2 ON t2.TeamID = m.Team2ID
                    JOIN Players p ON p.TeamID = t2.TeamID AND p.PlayerID = pm.PlayerID
                    WHERE m.MatchID = ?
                    GROUP BY m.MatchID
                ) AS team2_scores ON m.MatchID = team2_scores.MatchID
                SET m.ScoreTeam2 = team2_scores.Team2Goals
            `;

            db.query(updateTeam2ScoresQuery, [matchId], (err, result) => {
                if (err) {
                    return db.rollback(() => {
                        console.error('Error updating Team2 scores:', err);
                        res.status(500).json({ error: 'Failed to update Team2 scores' });
                    });
                }

                // Commit transaction
                db.commit((err) => {
                    if (err) {
                        return db.rollback(() => {
                            console.error('Error committing transaction:', err);
                            res.status(500).json({ error: 'Failed to commit transaction' });
                        });
                    }

                    res.json({ message: 'Match scores updated successfully' });
                });
            });
        });
    });
});




// Start the server
const PORT = process.env.PORT || 3000;
server.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on port ${PORT}.`);
});

